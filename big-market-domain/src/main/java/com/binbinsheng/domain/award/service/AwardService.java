package com.binbinsheng.domain.award.service;

import com.binbinsheng.domain.award.event.SendAwardMessageEvent;
import com.binbinsheng.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.binbinsheng.domain.award.model.entity.DistributeAwardEntity;
import com.binbinsheng.domain.award.model.entity.TaskEntity;
import com.binbinsheng.domain.award.model.entity.UserAwardRecordEntity;
import com.binbinsheng.domain.award.model.valobj.TaskStateVO;
import com.binbinsheng.domain.award.repository.IAwardRepository;
import com.binbinsheng.domain.award.service.distribute.IDistributeAward;
import com.binbinsheng.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AwardService implements IAwardService{

    @Resource
    SendAwardMessageEvent sendAwardMessageEvent;

    @Resource
    IAwardRepository repository;

    private final Map<String, IDistributeAward> distributeAwardMap;

    public AwardService(Map<String, IDistributeAward> distributeAwardMap) {
        this.distributeAwardMap = distributeAwardMap;
    }


    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {

        //构建消息对象
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = SendAwardMessageEvent.SendAwardMessage.builder()
                .userId(userAwardRecordEntity.getUserId())
                .awardId(userAwardRecordEntity.getAwardId())
                .awardConfig(userAwardRecordEntity.getAwardConfig())
                .orderId(userAwardRecordEntity.getOrderId())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .build();

        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage
                = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);

        //构建任务对象
        TaskEntity taskEntity = TaskEntity.builder()
                .userId(userAwardRecordEntity.getUserId())
                .topic(sendAwardMessageEvent.topic())
                .messageId(sendAwardMessageEventMessage.getId())
                .message(sendAwardMessageEventMessage)
                .state(TaskStateVO.create)
                .build();

        //构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .taskEntity(taskEntity)
                .build();


        //存储聚合对象，一个事务下，用户的中奖记录
        repository.saveUserAwardRecord(userAwardRecordAggregate);

    }

    @Override
    public void distributeAward(DistributeAwardEntity distributeAwardEntity) {
        // 奖品Key
        String awardKey = repository.queryAwardKeyByAwardId(distributeAwardEntity.getAwardId());
        if (null == awardKey) {
            log.error("分发奖品，奖品ID不存在。awardKey:{}", awardKey);
            return;
        }

        // 奖品服务
        IDistributeAward distributeAward = distributeAwardMap.get(awardKey);

        if (null == distributeAward) {
            log.error("分发奖品，对应的服务不存在。awardKey:{}", awardKey);
            throw new RuntimeException("分发奖品，奖品" + awardKey + "对应的服务不存在");
        }

        // 发放奖品
        distributeAward.getOutPrizes(distributeAwardEntity);

    }
}
