package com.binbinsheng.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.binbinsheng.domain.activity.model.entity.SkuRechargeEntity;
import com.binbinsheng.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.binbinsheng.domain.credit.model.entity.TradeEntity;
import com.binbinsheng.domain.credit.model.valobj.TradeNameVO;
import com.binbinsheng.domain.credit.model.valobj.TradeTypeVO;
import com.binbinsheng.domain.credit.repository.ICreditRepository;
import com.binbinsheng.domain.credit.service.ICreditAdjustService;
import com.binbinsheng.domain.rebate.event.SendRebateMessageEvent;
import com.binbinsheng.domain.rebate.model.valobj.RebateTypeVO;
import com.binbinsheng.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

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

    @Resource//给用户加积分
    private ICreditAdjustService creditAdjustService;
    
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

            switch (rebateMessage.getRebateType()) {
                case "sku"://加抽奖次数
                    SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                    skuRechargeEntity.setUserId(rebateMessage.getUserId());
                    skuRechargeEntity.setSku(Long.valueOf(rebateMessage.getRebateConfig()));
                    skuRechargeEntity.setOutBusinessNo(rebateMessage.getBizId());
                    raffleActivityAccountQuotaService.createOrder(skuRechargeEntity);
                    break;
                case "integral"://加积分
                    TradeEntity tradeEntity = new TradeEntity();
                    tradeEntity.setUserId(rebateMessage.getUserId());
                    tradeEntity.setTradeName(TradeNameVO.REBATE);
                    tradeEntity.setTradeType(TradeTypeVO.FORWARD);
                    tradeEntity.setAmount(new BigDecimal(rebateMessage.getRebateConfig()));
                    tradeEntity.setOutBusinessNo(rebateMessage.getBizId());
                    creditAdjustService.createOrder(tradeEntity);
                    break;
            }

        }catch (Exception e){
            log.error("监听用户行为返利信息，消费失败 topic:{} message:{}",topic,message,e );
            throw e;
        }
    }
    
}
