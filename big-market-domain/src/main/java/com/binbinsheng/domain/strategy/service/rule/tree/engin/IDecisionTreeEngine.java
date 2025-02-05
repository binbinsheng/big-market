package com.binbinsheng.domain.strategy.service.rule.tree.engin;

import com.binbinsheng.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

import java.util.Date;

/**
 * 规则树组合接口, 调用被工厂组装的规则树
 */
public interface IDecisionTreeEngine {

    DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId, Date endDate);


}
