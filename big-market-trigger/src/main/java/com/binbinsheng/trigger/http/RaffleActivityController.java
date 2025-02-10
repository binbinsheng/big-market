package com.binbinsheng.trigger.http;

import com.alibaba.fastjson.JSON;
import com.binbinsheng.domain.activity.model.entity.ActivityAccountEntity;
import com.binbinsheng.domain.activity.model.entity.UserRaffleOrderEntity;
import com.binbinsheng.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.binbinsheng.domain.activity.service.IRaffleActivityPartakeService;
import com.binbinsheng.domain.activity.service.armory.IActivityArmory;
import com.binbinsheng.domain.award.model.entity.UserAwardRecordEntity;
import com.binbinsheng.domain.award.model.valobj.AwardStateVO;
import com.binbinsheng.domain.award.service.IAwardService;
import com.binbinsheng.domain.rebate.model.entity.BehaviorEntity;
import com.binbinsheng.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.binbinsheng.domain.rebate.model.valobj.BehaviorTypeVO;
import com.binbinsheng.domain.rebate.repository.IBehaviorRebateRepository;
import com.binbinsheng.domain.rebate.service.BehaviorRebateService;
import com.binbinsheng.domain.rebate.service.IBehaviorRebateService;
import com.binbinsheng.domain.strategy.model.entity.RaffleAwardEntity;
import com.binbinsheng.domain.strategy.model.entity.RaffleFactorEntity;
import com.binbinsheng.domain.strategy.service.IRaffleStrategy;
import com.binbinsheng.domain.strategy.service.armory.IStrategyArmory;
import com.binbinsheng.trigger.api.IRaffleActivityService;
import com.binbinsheng.trigger.api.dto.ActivityDrawRequestDTO;
import com.binbinsheng.trigger.api.dto.ActivityDrawResponseDTO;
import com.binbinsheng.trigger.api.dto.UserActivityAccountRequestDTO;
import com.binbinsheng.trigger.api.dto.UserActivityAccountResponseDTO;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import com.binbinsheng.types.model.Response;
import jdk.nashorn.internal.ir.CallNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 抽奖活动服务
 */
@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/activity")
public class RaffleActivityController implements IRaffleActivityService {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    @Resource
    IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    @Resource
    IActivityArmory activityArmory;
    @Resource
    IStrategyArmory strategyArmory;
    @Resource
    IRaffleActivityPartakeService activityPartakeService;
    @Resource
    IRaffleStrategy raffleStrategy;
    @Resource
    IAwardService awardService;
    @Resource
    IBehaviorRebateService behaviorRebateService;

    /**
     * 活动装配 - 数据预热 | 把活动配置的对应的 sku 一起装配
     *
     * @param activityId 活动ID
     * @return 装配结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/armory">/api/v1/raffle/activity/armory</a>
     * 入参：{"activityId":100001,"userId":"xiaofuge"}
     *
     * curl --request GET \
     *   --url 'http://localhost:8091/api/v1/raffle/activity/armory?activityId=100301'
     */
    @RequestMapping(value = "armory", method = RequestMethod.GET)
    @Override
    public Response<Boolean> armory(Long activityId) {
        try {
            log.info("活动装配，数据预热，开始 activityId:{}", activityId);
            // 1. 活动装配
            activityArmory.assembleActivitySkuByActivityId(activityId);
            // 2. 策略装配
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);
            Response<Boolean> response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
            log.info("活动装配，数据预热，完成 activityId:{}", activityId);
            return response;
        } catch (Exception e) {
            log.error("活动装配，数据预热，失败 activityId:{}", activityId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }

    }


    /**
     * 抽奖接口
     *
     * @param request 请求对象
     * @return 抽奖结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/draw">/api/v1/raffle/activity/draw</a>
     * 入参：{"activityId":100001,"userId":"xiaofuge"}
     *
     * curl --request POST \
     *   --url http://localhost:8091/api/v1/raffle/activity/draw \
     *   --header 'content-type: application/json' \
     *   --data '{
     *     "userId":"xiaofuge",
     *     "activityId": 100301
     * }'
     */
    @RequestMapping(value = "draw", method = RequestMethod.POST)
    @Override
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDTO request) {
        try{
            log.info("活动抽奖 userId:{} activityId:{}", request.getUserId(), request.getActivityId());

            // 1. 参数校验
            if (StringUtils.isBlank(request.getUserId()) || request.getActivityId() == null){
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            // 2. 参与活动 - 创建参与记录订单
            UserRaffleOrderEntity userRaffleOrderEntity = activityPartakeService.createOrder(request.getUserId(), request.getActivityId());
            log.info("活动抽奖，创建订单 userId:{} activityId:{} orderId:{}", request.getUserId(), request.getActivityId(), userRaffleOrderEntity.getOrderId());

            // 3. 抽奖策略 - 执行抽奖
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId(userRaffleOrderEntity.getUserId())
                    .strategyId(userRaffleOrderEntity.getStrategyId())
                    .endDateTime(userRaffleOrderEntity.getEndDateTime())
                    .build());

            // 4. 存放结果 - 写入中奖记录
            UserAwardRecordEntity userAwardRecord = UserAwardRecordEntity.builder()
                    .userId(userRaffleOrderEntity.getUserId())
                    .activityId(userRaffleOrderEntity.getActivityId())
                    .strategyId(userRaffleOrderEntity.getStrategyId())
                    .orderId(userRaffleOrderEntity.getOrderId())
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardTime(new Date())
                    .awardState(AwardStateVO.create)
                    .build();
            awardService.saveUserAwardRecord(userAwardRecord);

            // 5.返回结果
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ActivityDrawResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
        }catch (AppException e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }

    }


    /**
     * 日历签到返利接口
     *
     * @param userId 用户ID
     * @return 签到返利结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/calendar_sign_rebate">/api/v1/raffle/activity/calendar_sign_rebate</a>
     * 入参：xiaofuge
     * <p>
     * curl -X POST http://localhost:8091/api/v1/raffle/activity/calendar_sign_rebate -d "userId=xiaofuge" -H "Content-Type: application/x-www-form-urlencoded"
     */
    @Override
    @RequestMapping(value = "calendar_sign_rebate", method = RequestMethod.POST)
    public Response<Boolean> calendarSignRebate(String userId) {
        try {
            log.info("日历签到返利开始 userId:{}", userId);
            BehaviorEntity behaviorEntity = new BehaviorEntity();
            behaviorEntity.setUserId(userId);
            behaviorEntity.setBehaviorType(BehaviorTypeVO.SIGN);
            behaviorEntity.setOutBusinessNo(dateFormat.format(new Date()));
            //虽然返回多个OrderId, 如由签到引起的返利：加次数(sku)，加积分(Integral)但是目前只有加次数生效
            List<String> orderIds = behaviorRebateService.createOrder(behaviorEntity);
            log.info("日历签到返利完成 userId:{} orderIds:{}", userId, JSON.toJSONString(orderIds));
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        }catch (AppException e) {
            log.error("日历签到返利异常 userId:{}", userId);
            return Response.<Boolean>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e) {
            log.error("日历签到返利失败 userId:{}", userId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "is_calender_sign_rebate", method = RequestMethod.POST)
    public Response<Boolean> isCalenderSignRebate(String userId) {
        try {
            log.info("查询用户是否完成日历签到返利-开始 userId:{}", userId);
            String outBusinessNo = dateFormat.format(new Date());
            List<UserBehaviorRebateOrderEntity> userBehaviorRebateOrderEntities
                    = behaviorRebateService.queryOrderByBusinessNo(userId, outBusinessNo);
            log.info("查询用户是否完成日历签到返利-完成 userId:{} orders.size:{}", userId, userBehaviorRebateOrderEntities.size());
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(!userBehaviorRebateOrderEntities.isEmpty())
                    .build();
        }catch (Exception e) {
            log.error("查询用户是否完成日历签到返利-失败 userId:{}", userId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "query_user_activity_account")
    public Response<UserActivityAccountResponseDTO> queryUserActivityAccount(UserActivityAccountRequestDTO requestDTO) {
        try{
            log.info("查询用户活动账户抽奖剩余次数-开始 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId());
            // 1. 参数校验
            if (StringUtils.isBlank(requestDTO.getUserId()) || null == requestDTO.getActivityId()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            ActivityAccountEntity activityAccountEntity = raffleActivityAccountQuotaService.queryActivityAccountEntity(requestDTO.getActivityId(), requestDTO.getUserId());
            UserActivityAccountResponseDTO userActivityAccountResponseDTO = UserActivityAccountResponseDTO.builder()
                    .totalCount(activityAccountEntity.getTotalCount())
                    .totalCountSurplus(activityAccountEntity.getTotalCountSurplus())
                    .dayCount(activityAccountEntity.getDayCount())
                    .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                    .monthCount(activityAccountEntity.getMonthCount())
                    .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                    .build();
            log.info("查询用户活动账户完成 userId:{} activityId:{} dto:{}", requestDTO.getUserId(), requestDTO.getActivityId(), JSON.toJSONString(userActivityAccountResponseDTO));
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(userActivityAccountResponseDTO)
                    .build();

        }
        catch (Exception e) {
            log.error("查询用户活动账户失败 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId(), e);
            return Response.<UserActivityAccountResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
