package com.binbinsheng.domain.award.event;

import com.binbinsheng.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MQ发送抽到的奖品消息
 */
@Component
public class SendAwardMessageEvent extends BaseEvent<SendAwardMessageEvent.SendAwardMessage> {

    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;


    @Override
    public EventMessage<SendAwardMessage> buildEventMessage(SendAwardMessage data) {
        return EventMessage.<SendAwardMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

    /*
       定义消息体
    */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class SendAwardMessage{
        //用户Id
        private String userId;

        //奖品Id
        private Integer awardId;

        //奖品标题
        private String awardTitle;
    }
}
