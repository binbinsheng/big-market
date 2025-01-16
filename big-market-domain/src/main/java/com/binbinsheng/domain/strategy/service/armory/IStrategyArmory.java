package com.binbinsheng.domain.strategy.service.armory;
/*
策略装配库（兵工厂），负责初始化策略计算
 */

public interface IStrategyArmory {


    boolean assembleLotteryStrategy(Long strategyId);

    Integer getRandomAwardId(Long strategyId);

}
