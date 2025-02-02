package com.binbinsheng.domain.award.model.aggregate;

import com.binbinsheng.domain.award.model.entity.TaskEntity;
import com.binbinsheng.domain.award.model.entity.UserAwardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户中奖记录聚合对象【聚合代表一个事务操作】
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordAggregate {
    private UserAwardRecordEntity userAwardRecordEntity;
    private TaskEntity taskEntity;
}
