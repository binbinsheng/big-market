package com.binbinsheng.domain.activity.service.rule;


/**
 * 抽奖动作责任链装配
 */

public interface IActionChainArmory {

    IActionChain appendNext(IActionChain next);

    IActionChain next();
}
