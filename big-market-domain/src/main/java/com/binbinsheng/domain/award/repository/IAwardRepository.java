package com.binbinsheng.domain.award.repository;

import com.binbinsheng.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.binbinsheng.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * 奖品仓储服务
 */

public interface IAwardRepository {

    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

    String queryAwardConfigByAwardId(Integer awardId);

    String queryAwardKeyByAwardId(Integer awardId);

    void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate);

}
