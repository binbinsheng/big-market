package com.binbinsheng.domain.strategy.service.rule;

import com.binbinsheng.domain.strategy.model.valobj.RuleWeightVO;

import java.util.List;
import java.util.Map;

/**
 * 查询抽奖规则配置(rule_tree_node中的rule_value)
 */

public interface IRaffleRule {

    Map<String, Integer> queryAwardRuleLockCount(String[] treeIds);

    List<RuleWeightVO> queryAwardRuleWeight(Long strategyId);

    List<RuleWeightVO> queryAwardRuleWeightByActivityId(Long activityId);

}
