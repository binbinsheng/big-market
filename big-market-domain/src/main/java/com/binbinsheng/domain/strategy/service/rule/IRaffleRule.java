package com.binbinsheng.domain.strategy.service.rule;

import java.util.Map;

/**
 * 查询抽奖规则配置(rule_tree_node中的rule_value)
 */

public interface IRaffleRule {

    Map<String, Integer> queryAwardRuleLockCount(String[] treeIds);

}
