package com.binbinsheng.infrastructure.event;


import com.alibaba.fastjson2.JSON;
import com.binbinsheng.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息发送
 */

@Component
@Slf4j
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publish(String topic, BaseEvent.EventMessage<?> eventMessage) {
        try{
            //序列化为JSON
            String messageJson = JSON.toJSONString(eventMessage);
            /**
             * 这表明这个类是用来与RabbitMQ交互的，负责发布消息。RabbitTemplate是
             * Spring AMQP提供的用于发送和接收消息的核心类，封装了与RabbitMQ的交互细节，简化了操作。
             */
            // 发送到 RabbitMQ
            //topic：消息的路由键（Routing Key），结合 Exchange 类型决定消息路由到哪个队列
            //messageJson：消息内容（JSON 格式字符串）。
            //默认行为：使用默认的 Exchange（名为空字符串的 Direct Exchange），直接将消息发送到与 topic 同名的队列。
            rabbitTemplate.convertAndSend(topic, messageJson);
            log.info("发送MQ消息成功 topic:{}, message:{}", topic, messageJson);
        }catch (Exception e){
            log.error("发送MQ消息失败 topic:{}, message:{}", topic, JSON.toJSONString(eventMessage),e);
            throw e;
        }
    }

    public void publish(String topic, String eventMessage) {
        try{
            /**
             * 这表明这个类是用来与RabbitMQ交互的，负责发布消息。RabbitTemplate是
             * Spring AMQP提供的用于发送和接收消息的核心类，封装了与RabbitMQ的交互细节，简化了操作。
             */
            // 发送到 RabbitMQ
            //topic：消息的路由键（Routing Key），结合 Exchange 类型决定消息路由到哪个队列
            //messageJson：消息内容（JSON 格式字符串）。
            //默认行为：使用默认的 Exchange（名为空字符串的 Direct Exchange），直接将消息发送到与 topic 同名的队列。
            rabbitTemplate.convertAndSend(topic, eventMessage);
            log.info("发送MQ消息成功 topic:{}, message:{}", topic, eventMessage);
        }catch (Exception e){
            log.error("发送MQ消息失败 topic:{}, message:{}", topic, eventMessage,e);
            throw e;
        }
    }
}
