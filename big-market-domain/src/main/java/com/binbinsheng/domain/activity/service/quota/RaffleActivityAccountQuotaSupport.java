package com.binbinsheng.domain.activity.service.quota;

import com.binbinsheng.domain.activity.model.entity.ActivityCountEntity;
import com.binbinsheng.domain.activity.model.entity.ActivityEntity;
import com.binbinsheng.domain.activity.model.entity.ActivitySkuEntity;
import com.binbinsheng.domain.activity.respository.IActivityRepository;
import com.binbinsheng.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;

/**
 * 抽奖活动支持类
 */

public class RaffleActivityAccountQuotaSupport {

    protected IActivityRepository repository;
    protected DefaultActivityChainFactory defaultChainFactory;

    public RaffleActivityAccountQuotaSupport(IActivityRepository repository, DefaultActivityChainFactory defaultChainFactory) {
        this.repository = repository;
        this.defaultChainFactory = defaultChainFactory;
    }

    public ActivitySkuEntity queryActivitySku(Long sku) {
        return repository.queryActivitySku(sku);
    }

    public ActivityEntity queryActivityByActivityId(Long activityId) {
        return repository.queryRaffleActivityByActivityId(activityId);
    }

    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityId) {
        return repository.queryRaffleActivityCountByActivityCountId(activityId);
    }
}
