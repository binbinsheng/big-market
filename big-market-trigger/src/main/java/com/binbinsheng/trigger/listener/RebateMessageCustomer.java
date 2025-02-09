package com.binbinsheng.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.binbinsheng.domain.activity.model.entity.SkuRechargeEntity;
import com.binbinsheng.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.binbinsheng.domain.rebate.event.SendRebateMessageEvent;
import com.binbinsheng.domain.rebate.model.valobj.RebateTypeVO;
import com.binbinsheng.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监听：行为返利消息
 */
@Slf4j
@Component
public class RebateMessageCustomer {

    
    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;
    
    @Resource//给用户账户增加额度
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    
    
    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void listener(String message) {
        try{
            log.info("监听用户行为返利信息 topic:{} message:{}",topic,message);
            //1. 转化message String->BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>
            /**
             * TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>是一个抽象类
             * new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {
             *             }.getType()实例化了它的一个匿名子类并获得类型
             */
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {
            }.getType());
            SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();
            if (!rebateMessage.getRebateType().equals(RebateTypeVO.SKU.getCode())){
                log.info("监听用户行为返利消息 - 非sku奖励暂时不处理 topic:{} message:{}",topic,message);
                return;
            }

            //2. 创建入账奖励
            SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
            skuRechargeEntity.setUserId(rebateMessage.getUserId());
            skuRechargeEntity.setSku(Long.parseLong(rebateMessage.getRebateConfig())); //RebateConfig指定了sku的单号
            skuRechargeEntity.setOutBusinessNo(rebateMessage.getBizId());
            raffleActivityAccountQuotaService.createOrder(skuRechargeEntity);


        }catch (Exception e){
            log.error("监听用户行为返利信息，消费失败 topic:{} message:{}",topic,message,e );
            throw e;
        }
    }
    
}
