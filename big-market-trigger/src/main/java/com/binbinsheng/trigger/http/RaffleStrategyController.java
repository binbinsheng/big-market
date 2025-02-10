package com.binbinsheng.trigger.http;

import com.alibaba.fastjson2.JSON;
import com.binbinsheng.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.binbinsheng.domain.strategy.model.entity.RaffleAwardEntity;
import com.binbinsheng.domain.strategy.model.entity.RaffleFactorEntity;
import com.binbinsheng.domain.strategy.model.entity.StrategyAwardEntity;
import com.binbinsheng.domain.strategy.model.valobj.RuleWeightVO;
import com.binbinsheng.domain.strategy.service.IRaffleAward;
import com.binbinsheng.domain.strategy.service.IRaffleStrategy;
import com.binbinsheng.domain.strategy.service.armory.IStrategyArmory;
import com.binbinsheng.domain.strategy.service.rule.IRaffleRule;
import com.binbinsheng.trigger.api.IRaffleStrategyService;
import com.binbinsheng.trigger.api.dto.*;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import com.binbinsheng.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 抽奖服务
 */

@Slf4j
@RestController
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy")
public class RaffleStrategyController implements IRaffleStrategyService {

    @Resource
    IRaffleStrategy raffleStrategy;

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleAward raffleAward;

    @Resource
    private IRaffleRule raffleRule;

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    /**
     * 策略装配，将策略信息装配到缓存中
     * @param strategyId
     * @return
     */
    @RequestMapping(value = "strategy_armory", method = RequestMethod.GET)
    @Override
    public Response<Boolean> strategyArmory(Long strategyId) {

        try {
            log.info("抽奖策略装配开始 strategyId:{}", strategyId);
            boolean armoryStatus = strategyArmory.assembleLotteryStrategy(strategyId);
            Response response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(armoryStatus)
                    .build();
            log.info("抽奖策略装配完成 strategyId:{} response:{}", strategyId, JSON.toJSONString(response));
            return response;

        } catch (Exception e) {
            log.error("抽奖策略装配失败 strategyId:{}", strategyId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "query_raffle_strategy_rule_weight", method = RequestMethod.POST)
    public Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(@RequestBody RaffleStrategyRuleWeightRequestDTO requestDTO) {
        try {
            log.info("查询抽奖策略权重规则配置开始 userId:{} activityId：{}", requestDTO.getUserId(), requestDTO.getActivityId());
            // 1. 参数校验
            if (StringUtils.isBlank(requestDTO.getUserId()) || null == requestDTO.getActivityId()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            // 2. 查询用户抽奖总次数
            Integer userActivityAccountTotalUseCount
                    = raffleActivityAccountQuotaService.queryRaffleActivityAccountPartakeCount(requestDTO.getActivityId(), requestDTO.getUserId());
            // 3. 查询规则
            List<RuleWeightVO> ruleWeightVOS = raffleRule.queryAwardRuleWeightByActivityId(requestDTO.getActivityId());

            ArrayList<RaffleStrategyRuleWeightResponseDTO> raffleStrategyRuleWeightList = new ArrayList<>();
            for (RuleWeightVO ruleWeightVO : ruleWeightVOS) {
                // 转换对象
                List<RaffleStrategyRuleWeightResponseDTO.StrategyAward> strategyAwards = new ArrayList<>();
                List<RuleWeightVO.Award> awardList = ruleWeightVO.getAwardList();
                for (RuleWeightVO.Award award : awardList) {
                    RaffleStrategyRuleWeightResponseDTO.StrategyAward strategyAward = new RaffleStrategyRuleWeightResponseDTO.StrategyAward();
                    strategyAward.setAwardId(award.getAwardId());
                    strategyAward.setAwardTitle(award.getAwardTitle());
                    strategyAwards.add(strategyAward);
                }
                // 封装对象
                RaffleStrategyRuleWeightResponseDTO raffleStrategyRuleWeightResponseDTO = new RaffleStrategyRuleWeightResponseDTO();
                raffleStrategyRuleWeightResponseDTO.setRuleWeightCount(ruleWeightVO.getWeight());
                raffleStrategyRuleWeightResponseDTO.setStrategyAwards(strategyAwards);
                raffleStrategyRuleWeightResponseDTO.setUserActivityAccountTotalUseCount(userActivityAccountTotalUseCount);

                raffleStrategyRuleWeightList.add(raffleStrategyRuleWeightResponseDTO);

            }
            Response<List<RaffleStrategyRuleWeightResponseDTO>> response = Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleStrategyRuleWeightList)
                    .build();
            log.info("查询抽奖策略权重规则配置完成 userId:{} activityId：{} response: {}", requestDTO.getUserId(), requestDTO.getActivityId(), JSON.toJSONString(response));
            return response;


        }catch (Exception e) {
            log.error("查询抽奖策略权重规则配置失败 userId:{} activityId：{}", requestDTO.getUserId(), requestDTO.getActivityId(), e);
            return Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();

        }

    }


    @RequestMapping(value = "query_raffle_award_list", method = RequestMethod.POST)
    @Override
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO requestDTO) {

        try {
            log.info("查询抽奖奖品列表配置开始 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId());
            //1. 参数校验
            if (StringUtils.isBlank(requestDTO.getUserId()) || requestDTO.getActivityId() == null){
                return Response.<List<RaffleAwardListResponseDTO>>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 2. 查询奖品配置
            List<StrategyAwardEntity>  strategyAwardEntities = raffleAward.queryRaffleStrategyAwardListByActivityId(requestDTO.getActivityId());
            // 3. 获取规则
            String[] treeIds = strategyAwardEntities.stream()
                    .map(StrategyAwardEntity::getRuleModels)
                    .filter(ruleModel -> ruleModel != null && !ruleModel.isEmpty())
                    .toArray(String[]::new);
            // 4. 查询规则配置 - 获取奖品的解锁限制:抽奖N次后解锁（rule_tree_node)
            /**
             * ruleLockCountMap = {rule_lock_1:1, rule_lock_2:2};
             */
            Map<String, Integer> ruleLockCountMap = raffleRule.queryAwardRuleLockCount(treeIds);
            // 5. 查询抽奖次数 - 用户已经参与抽奖的次数
            Integer dayPartakeCount = raffleActivityAccountQuotaService.queryRaffleActivityAccountDayPartakeCount(requestDTO.getActivityId(), requestDTO.getUserId());

            // 6.遍历填充数据
            //将List<StrategyAwardEntity>实体类对象转为List<RaffleAwardListResponseDTO>
            ArrayList<RaffleAwardListResponseDTO> raffleAwardListResponseDTOs = new ArrayList<>(strategyAwardEntities.size());
            for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
                Integer awardRuleLockCount = ruleLockCountMap.get(strategyAwardEntity.getRuleModels());
                raffleAwardListResponseDTOs.add(RaffleAwardListResponseDTO.builder()
                                .awardId(strategyAwardEntity.getAwardId())
                                .awardTitle(strategyAwardEntity.getAwardTitle())
                                .awardSubtitle(strategyAwardEntity.getAwardSubtitle())
                                .sort(strategyAwardEntity.getSort())
                                .awardRuleLockCount(awardRuleLockCount)
                                .isAwardUnlock(null == awardRuleLockCount || dayPartakeCount > awardRuleLockCount? true : false)
                                .waitUnlockCount(null == awardRuleLockCount || awardRuleLockCount <= dayPartakeCount ? 0 : (awardRuleLockCount - dayPartakeCount))
                                .build());
            }
            Response<List<RaffleAwardListResponseDTO>> response = Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleAwardListResponseDTOs)
                    .build();
            log.info("查询抽奖奖品列表配置完成 userId:{} activityId:{} response:{}", requestDTO.getUserId(),requestDTO.getActivityId(), JSON.toJSONString(response));
            return response;

        } catch (Exception e) {
            log.error("查询抽奖奖品列表配置失败 userId:{} activityId:{}", requestDTO.getUserId(), requestDTO.getActivityId());
            return Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }



    }

    @RequestMapping(value = "random_raffle", method = RequestMethod.POST)
    @Override
    public Response<RaffleStrategyResponseDTO> randomRaffle(@RequestBody RaffleStrategyRequestDTO requestDTO) {
        try {
            log.info("随机抽奖开始 strategyId:{}", requestDTO.getStrategyId());
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .strategyId(requestDTO.getStrategyId())
                    .userId("system")
                    .build());
            Response<RaffleStrategyResponseDTO> response = Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleStrategyResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
            log.info("随机抽奖结束 strategyId:{} response:{}", requestDTO.getStrategyId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("随机抽奖配置失败 strategyId:{}", requestDTO.getStrategyId());
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
