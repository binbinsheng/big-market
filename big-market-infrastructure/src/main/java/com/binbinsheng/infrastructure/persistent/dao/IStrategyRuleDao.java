package com.binbinsheng.infrastructure.persistent.dao;

import com.binbinsheng.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IStrategyRuleDao {

    List<StrategyRule> queryStrategyRuleList();


    StrategyRule queryStrategyRule(StrategyRule strategyRuleReq);

    String queryStrategyRuleValue(StrategyRule strategyRuleReq);
}
