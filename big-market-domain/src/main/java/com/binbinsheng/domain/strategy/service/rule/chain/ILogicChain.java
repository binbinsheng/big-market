package com.binbinsheng.domain.strategy.service.rule.chain;

import com.binbinsheng.domain.strategy.service.rule.chain.factory.DefaultChainFactory;

/**
 * 责任链接口
 */

public interface ILogicChain extends ILogicChainArmory {

    /**
     * userId 用户ID
     * strategyId 策略Id
     * return 返回的奖品对象->(Integer awardId, String logicModel<-指明返回的是黑名单，权重还是默认)
     */

    DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId);



}
