package com.binbinsheng.domain.award.repository;

import com.binbinsheng.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * 奖品仓储服务
 */

public interface IAwardRepository {

    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

}
