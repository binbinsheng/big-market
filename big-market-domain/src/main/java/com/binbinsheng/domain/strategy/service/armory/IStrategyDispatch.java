package com.binbinsheng.domain.strategy.service.armory;

/*
策略抽獎調度
 */

public interface IStrategyDispatch {

    Integer getRandomAwardId(Long strategyId);

    /*这种情况下，抽奖时需要检验用户积分多少，在对应的奖品表里抽奖*/
    Integer getRandomAwardId(Long strategyId, String ruleWeightValues);

    public Boolean subtractAwardStock(Long strategyId, Integer awardId);
}
