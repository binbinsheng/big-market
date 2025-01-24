package com.binbinsheng.domain.strategy.service;

import com.binbinsheng.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * 策略奖品接口，供API调用在前端显示
 */

public interface IRaffleAward {

    List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);

}
