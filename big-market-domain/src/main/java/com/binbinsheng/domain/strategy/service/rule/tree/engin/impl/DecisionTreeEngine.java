package com.binbinsheng.domain.strategy.service.rule.tree.engin.impl;

import com.binbinsheng.domain.strategy.model.valobj.*;
import com.binbinsheng.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.binbinsheng.domain.strategy.service.rule.tree.engin.IDecisionTreeEngine;
import com.binbinsheng.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.google.common.eventbus.DeadEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 规则树引擎
 */

@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    /**
     * logicTreeNodeGroup = {"rule_lock" : RuleLockLogicTreeNode.java
     *                       "rule_luck_award" : RuleLuckAwardLogicTreeNode.java,
     *                       "rule_stock" : RuleStockLogicTreeNode.java }
     */
    private final Map<String, ILogicTreeNode> logicTreeNodeGroup;

    //那棵树
    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeGroup, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeGroup = logicTreeNodeGroup;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {

        DefaultTreeFactory.StrategyAwardVO strategyAwardData = null;

        //获取基础信息
        //获取根节点
        String nextNode = ruleTreeVO.getTreeRootRuleNode();

        //treeNodeMap = Map<String, RuleTreeNodeVO>
        /**
         *    在测试类中设置了TreeNodeMap
         *    ruleTreeVO.setTreeNodeMap(new HashMap<String, RuleTreeNodeVO>() {{
         *        put("rule_lock", rule_lock <-- 对应的树结点);
         *        put("rule_stock", rule_stock);
         *        put("rule_luck_award", rule_luck_award);
         *    }});
         */
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        //获得根节点对应的RuleTreeNodeVO对象 rule_lock
        RuleTreeNodeVO ruleTreeNode = treeNodeMap.get(nextNode);

        while (ruleTreeNode != null) {
            //拿到rule_lock对应的实现类RuleLockLogicTreeNode.java
            ILogicTreeNode LogicTreeNode = logicTreeNodeGroup.get((ruleTreeNode.getRuleKey()));

            /** logicEntity = DefaultTreeFactory.TreeActionEntity.builder()
                            .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                          .build();
             **/
            DefaultTreeFactory.TreeActionEntity logicEntity = LogicTreeNode.logic(userId, strategyId, awardId);
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = logicEntity.getRuleLogicCheckTypeVO();
            strategyAwardData = logicEntity.getStrategyAwardVO();
            log.info("决策树引擎【{}】 treeId:{} node:{} code:{}",ruleTreeVO.getTreeName(),
                    ruleTreeVO.getTreeId(), nextNode, ruleLogicCheckTypeVO.getCode());

            nextNode = FindNextNode(ruleLogicCheckTypeVO.getCode(), ruleTreeNode.getTreeNodeLineVOList());

            ruleTreeNode = treeNodeMap.get(nextNode);

        }

        //返回经过决策树过滤后的最终结果
        return strategyAwardData;

    }

    /**
                .treeNodeLineVOList(new ArrayList<RuleTreeNodeLineVO>() {{
        add(RuleTreeNodeLineVO.builder()
                .treeId(100000001)
                .ruleNodeFrom("rule_lock")
                .ruleNodeTo("rule_luck_award")
                .ruleLimitType(RuleLimitTypeVO.EQUAL)
                .ruleLimitValue(RuleLogicCheckTypeVO.TAKE_OVER)
                .build());

        add(RuleTreeNodeLineVO.builder()
                .treeId(100000001)
                .ruleNodeFrom("rule_lock")
                .ruleNodeTo("rule_stock")
                .ruleLimitType(RuleLimitTypeVO.EQUAL)
                .ruleLimitValue(RuleLogicCheckTypeVO.ALLOW)
                .build());
    }})
    **/

    private String FindNextNode(String matterValue,
                                List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList){
        if (ruleTreeNodeLineVOList == null || ruleTreeNodeLineVOList.isEmpty()){
            return null;
        }
        for (RuleTreeNodeLineVO nodeLine : ruleTreeNodeLineVOList) {
            if (decisionLogic(matterValue, nodeLine)){
                return nodeLine.getRuleNodeTo();
            }
        }
        throw new RuntimeException("决策树引擎, nextNode 找不到");
    }

    public boolean decisionLogic(String matterValue, RuleTreeNodeLineVO nodeLine) {
        switch (nodeLine.getRuleLimitType()) {
            case EQUAL:
                return matterValue.equals(nodeLine.getRuleLimitValue().getCode());
            // 以下规则暂时不需要实现
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }





}
