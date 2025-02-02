package com.binbinsheng.domain.award.service;

import com.binbinsheng.domain.award.event.SendAwardMessageEvent;
import com.binbinsheng.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.binbinsheng.domain.award.model.entity.TaskEntity;
import com.binbinsheng.domain.award.model.entity.UserAwardRecordEntity;
import com.binbinsheng.domain.award.model.valobj.TaskStateVO;
import com.binbinsheng.domain.award.repository.IAwardRepository;
import com.binbinsheng.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AwardService implements IAwardService{

    @Resource
    SendAwardMessageEvent sendAwardMessageEvent;

    @Resource
    IAwardRepository repository;

    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {

        //构建消息对象
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = SendAwardMessageEvent.SendAwardMessage.builder()
                .userId(userAwardRecordEntity.getUserId())
                .awardId(userAwardRecordEntity.getAwardId())
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
}
