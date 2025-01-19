package com.binbinsheng.domain.strategy.service.rule.chain;

/*
不想暴露appendNext和next给用户，所以从ILogic接口提取出这两个方法
 */

public interface ILogicChainArmory {

    ILogicChain appendNext(ILogicChain next);

    ILogicChain next();


}
