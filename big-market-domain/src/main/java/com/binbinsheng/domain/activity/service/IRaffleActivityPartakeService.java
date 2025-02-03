package com.binbinsheng.domain.activity.service;

import com.binbinsheng.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.binbinsheng.domain.activity.model.entity.UserRaffleOrderEntity;

public interface IRaffleActivityPartakeService {

    /**
     * 创建抽奖单：用户参与抽奖活动，扣减活动库存账户，产生抽奖单，如存在未被使用的抽奖单则直接返回已存在的抽奖单
     * @param partakeRaffleActivityEntity
     * @return 用户抽奖订单实体对象
     */
    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);

    UserRaffleOrderEntity createOrder(String userId, Long activityId);
}
