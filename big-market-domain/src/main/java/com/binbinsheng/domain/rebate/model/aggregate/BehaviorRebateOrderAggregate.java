package com.binbinsheng.domain.rebate.model.aggregate;

import com.binbinsheng.domain.rebate.model.entity.TaskEntity;
import com.binbinsheng.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行为返利聚合对象，包括用户Id，用户行为返利订单，任务 在一个事务下
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BehaviorRebateOrderAggregate {

    private String userId;

    private UserBehaviorRebateOrderEntity userBehaviorRebateOrder;

    private TaskEntity taskEntity;

}
