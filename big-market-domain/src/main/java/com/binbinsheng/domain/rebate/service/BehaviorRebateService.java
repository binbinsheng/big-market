package com.binbinsheng.domain.rebate.service;

import com.binbinsheng.domain.rebate.event.SendRebateMessageEvent;
import com.binbinsheng.domain.rebate.model.aggregate.BehaviorRebateOrderAggregate;
import com.binbinsheng.domain.rebate.model.entity.BehaviorEntity;
import com.binbinsheng.domain.rebate.model.entity.TaskEntity;
import com.binbinsheng.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.binbinsheng.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import com.binbinsheng.domain.rebate.model.valobj.TaskStateVO;
import com.binbinsheng.domain.rebate.repository.IBehaviorRebateRepository;
import com.binbinsheng.types.common.Constants;
import com.binbinsheng.types.event.BaseEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 行为返利服务实现
 */
@Service
public class BehaviorRebateService implements IBehaviorRebateService {

    @Resource
    IBehaviorRebateRepository repository;

    @Resource
    SendRebateMessageEvent sendRebateMessageEvent;

    @Override
    public List<String> createOrder(BehaviorEntity behaviorEntity) {

        //1. 查询返利配置
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = repository.queryDailyBehaviorRebateConfig(behaviorEntity.getBehaviorType());
        if (null == dailyBehaviorRebateVOS || dailyBehaviorRebateVOS.isEmpty()) {
            return new ArrayList<>();
        }
        //2. 构建聚合对象
        ArrayList<String> orderIds = new ArrayList<>();
        ArrayList<BehaviorRebateOrderAggregate> behaviorRebateOrderAggregates = new ArrayList<>();
        for (DailyBehaviorRebateVO dailyBehaviorRebateVO : dailyBehaviorRebateVOS) {
            String bizId = behaviorEntity.getUserId() + Constants.UNDERLINE +
                    dailyBehaviorRebateVO.getRebateType() + Constants.UNDERLINE + behaviorEntity.getOutBusinessNo();
            UserBehaviorRebateOrderEntity userBehaviorRebateOrderEntity = UserBehaviorRebateOrderEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .behaviorType(dailyBehaviorRebateVO.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                    .rebateType(dailyBehaviorRebateVO.getRebateType())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .outBusinessNo(behaviorEntity.getOutBusinessNo())
                    .bizId(bizId)
                    .build();
            orderIds.add(userBehaviorRebateOrderEntity.getOrderId());

            //构建RebateMessage消息体
            SendRebateMessageEvent.RebateMessage rebateMessage = SendRebateMessageEvent.RebateMessage.builder()
                    .userId(behaviorEntity.getUserId())
                    .rebateType(dailyBehaviorRebateVO.getRebateType())
                    .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .bizId(bizId)
                    .build();

            //构建事件消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> rebateMessageEventMessage
                    = sendRebateMessageEvent.buildEventMessage(rebateMessage);

            //组装任务对象
            TaskEntity taskEntity = TaskEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .messageId(rebateMessageEventMessage.getId())
                    .topic(sendRebateMessageEvent.topic())
                    .message(rebateMessageEventMessage)
                    .state(TaskStateVO.create)
                    .build();


            //构建聚合对象集合
            BehaviorRebateOrderAggregate behaviorRebateOrderAggregate = BehaviorRebateOrderAggregate.builder()
                    .userId(behaviorEntity.getUserId())
                    .userBehaviorRebateOrder(userBehaviorRebateOrderEntity)
                    .taskEntity(taskEntity)
                    .build();
            behaviorRebateOrderAggregates.add(behaviorRebateOrderAggregate);
        }

        //3. 存储聚合对象数据
        //穿userId是为了分库分表路由
        repository.saveUserRebateRecord(behaviorEntity.getUserId(), behaviorRebateOrderAggregates);


        //4. 返回订单Id集合
        return orderIds;
    }

    @Override
    public List<UserBehaviorRebateOrderEntity> queryOrderByBusinessNo(String userId, String businessNo) {
        return repository.queryOrderByBusinessNo(userId, businessNo);
    }
}