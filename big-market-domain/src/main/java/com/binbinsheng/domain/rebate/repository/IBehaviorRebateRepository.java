package com.binbinsheng.domain.rebate.repository;

import com.binbinsheng.domain.rebate.model.aggregate.BehaviorRebateOrderAggregate;
import com.binbinsheng.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.binbinsheng.domain.rebate.model.valobj.BehaviorTypeVO;
import com.binbinsheng.domain.rebate.model.valobj.DailyBehaviorRebateVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 行为返利服务仓储接口
 */

public interface IBehaviorRebateRepository {
    void saveUserRebateRecord(String userId, ArrayList<BehaviorRebateOrderAggregate> behaviorRebateOrderAggregates);

    List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(
            BehaviorTypeVO behaviorType);

    List<UserBehaviorRebateOrderEntity> queryOrderByBusinessNo(String userId, String businessNo);
}
