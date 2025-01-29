package com.binbinsheng.domain.activity.service.quota;

import com.binbinsheng.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.binbinsheng.domain.activity.model.entity.*;
import com.binbinsheng.domain.activity.respository.IActivityRepository;
import com.binbinsheng.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.binbinsheng.domain.activity.service.quota.rule.IActionChain;
import com.binbinsheng.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


/**
 * 抽奖活动抽象类，定义标准的流程
 */

@Slf4j
public abstract class AbstractRaffleActivityAccountQuotaAccountQuota extends RaffleActivityAccountQuotaSupport implements IRaffleActivityAccountQuotaService {

    public AbstractRaffleActivityAccountQuotaAccountQuota(IActivityRepository repository, DefaultActivityChainFactory defaultChainFactory) {
        super(repository, defaultChainFactory);
    }

    @Override
    public String createOrder(SkuRechargeEntity skuRechargeEntity) {
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
        CreateQuotaOrderAggregate createQuotaOrderAggregate = buildOrderAggregate(skuRechargeEntity, activitySkuEntity, activityEntity, activityCountEntity);

        //5.保存订单
        doSaveOrder(createQuotaOrderAggregate);

        return createQuotaOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    protected abstract void doSaveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate);

    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity,
                                                                     ActivityCountEntity activityCountEntity);
}
