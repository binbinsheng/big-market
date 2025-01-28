package com.binbinsheng.domain.activity.service;

import com.alibaba.fastjson.JSON;
import com.binbinsheng.domain.activity.model.aggregate.CreateOrderAggregate;
import com.binbinsheng.domain.activity.model.entity.*;
import com.binbinsheng.domain.activity.respository.IActivityRepository;
import com.binbinsheng.domain.activity.service.rule.IActionChain;
import com.binbinsheng.domain.activity.service.rule.factory.DefaultActivityChainFactory;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


/**
 * 抽奖活动抽象类，定义标准的流程
 */

@Slf4j
public abstract class AbstractRaffleActivity extends RaffleActivitySupport implements IRaffleOrder{

    public AbstractRaffleActivity(IActivityRepository repository, DefaultActivityChainFactory defaultChainFactory) {
        super(repository, defaultChainFactory);
    }

    @Override
    public String createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity) {
        //1. 参数校验
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        if (sku == null || StringUtils.isBlank(userId) || StringUtils.isBlank(outBusinessNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        //2.查询基础信息
        //2.1 通过sku查询活动信息
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        //2.2 查询活动信息
        ActivityEntity activityEntity = queryActivityByActivityId(activitySkuEntity.getActivityId());
        //2.3查询次数信息(用户在活动上可参与的次数)
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        //3.活动动作规则校验
        IActionChain actionChain = defaultChainFactory.openActionChain();
        //错误会抛异常
        actionChain.action(activitySkuEntity, activityEntity, activityCountEntity);

        //4.构建订单聚合对象
        CreateOrderAggregate createOrderAggregate = buildOrderAggregate(skuRechargeEntity, activitySkuEntity, activityEntity, activityCountEntity);

        //5.保存订单
        doSaveOrder(createOrderAggregate);

        return createOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    protected abstract void doSaveOrder(CreateOrderAggregate createOrderAggregate);

    protected abstract CreateOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity,
                                                                ActivityCountEntity activityCountEntity);
}
