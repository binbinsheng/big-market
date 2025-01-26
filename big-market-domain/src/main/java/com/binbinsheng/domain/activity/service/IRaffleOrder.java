package com.binbinsheng.domain.activity.service;

import com.binbinsheng.domain.activity.model.entity.ActivityOrderEntity;
import com.binbinsheng.domain.activity.model.entity.ActivityShopCartEntity;

public interface IRaffleOrder {

    /**
     * 以sku创建抽奖活动订单，获取参与抽奖资格(可消耗次数)
     * @param activityShopCartEntity 活动sku实体，通过sku领取活动
     * @return 活动参与记录实体
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);

}
