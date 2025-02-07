package com.binbinsheng.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import com.binbinsheng.infrastructure.persistent.po.UserBehaviorRebateOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户行为返利流水订单表
 */

@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserBehaviorRebateOrderDao {


    void insert(UserBehaviorRebateOrder userBehaviorRebateOrder);
}

