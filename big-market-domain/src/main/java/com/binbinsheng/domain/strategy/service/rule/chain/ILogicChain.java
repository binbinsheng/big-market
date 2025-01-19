package com.binbinsheng.domain.strategy.service.rule.chain;

/**
 * 责任链接口
 */

public interface ILogicChain extends ILogicChainArmory{

    /**
     * userId 用户ID
     * strategyId 策略Id
     * return 奖品ID
     */

    Integer logic(String userId, Long strategyId);



}
