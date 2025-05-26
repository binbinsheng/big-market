package com.binbinsheng.domain.award.service;

import com.binbinsheng.domain.award.model.entity.DistributeAwardEntity;
import com.binbinsheng.domain.award.model.entity.UserAwardRecordEntity;

/**
 * 奖品服务接口，将用户中奖记录保存到数据库task表中
 */

public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

    /**
     * 配送发货奖品
     */
    void distributeAward(DistributeAwardEntity distributeAwardEntity);

}
