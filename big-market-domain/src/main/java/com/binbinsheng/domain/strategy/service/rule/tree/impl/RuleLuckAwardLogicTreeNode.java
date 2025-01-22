package com.binbinsheng.domain.strategy.service.rule.tree.impl;

import com.binbinsheng.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.binbinsheng.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.binbinsheng.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.binbinsheng.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 兜底奖励结点
 */

@Slf4j
@Component("rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue){

        //rule_luck_award 的rule_value -> 101:1,100
        log.info("规则过滤-兜底奖品 userId:{} strategyId:{} awardId:{} ruleValue:{}"
                , userId, strategyId, awardId, ruleValue);

        String[] split = ruleValue.split(Constants.COLON);
        if (split.length == 0){
            log.error("规则过滤-兜底奖品，兜底奖品未配置警告 userId:{} strategyId:{} awardId:{}"
                    , userId, strategyId, awardId);
            throw new RuntimeException("兜底奖品未配置" + ruleValue);
        }

        //兜底奖品配置
        Integer luckAwardId = Integer.valueOf(split[0]);
        String awardRuleValue = split.length > 1? split[1] : "";

        //返回兜底奖品
        log.info("规则过滤-兜底奖品 serId:{} strategyId:{} awardId:{} awardRuleValue:{}",
                userId, strategyId, awardId, awardRuleValue);
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .awardRuleValue(awardRuleValue) //随机返回1-100内的积分，先写着，后续处理
                        .build())
                .build();

    }
}
