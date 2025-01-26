package com.binbinsheng.domain.activity.service;

import com.binbinsheng.domain.activity.model.entity.ActivityCountEntity;
import com.binbinsheng.domain.activity.model.entity.ActivityEntity;
import com.binbinsheng.domain.activity.model.entity.ActivitySkuEntity;
import com.binbinsheng.domain.activity.respository.IActivityRepository;
import com.binbinsheng.domain.activity.service.rule.factory.DefaultActivityChainFactory;
import com.binbinsheng.domain.strategy.service.rule.chain.factory.DefaultChainFactory;

/**
 * 抽奖活动支持类
 */

public class RaffleActivitySupport {

    protected IActivityRepository repository;
    protected DefaultActivityChainFactory defaultChainFactory;

    public RaffleActivitySupport(IActivityRepository repository, DefaultActivityChainFactory defaultChainFactory) {
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
