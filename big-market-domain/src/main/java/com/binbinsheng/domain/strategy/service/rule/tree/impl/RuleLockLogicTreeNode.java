package com.binbinsheng.domain.strategy.service.rule.tree.impl;

import com.binbinsheng.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.binbinsheng.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.binbinsheng.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 次数锁结点
 */

@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    //用户抽奖次数，后续完成这部分流程开发的时候，从数据库/Redis中读取
    private Long userRaffleCount = 0L;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue) {

        log.info("规则过滤-次数锁 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);

        long raffleCount = 0L;

        try {
            raffleCount  = Long.parseLong(ruleValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException("规则过滤-次数锁异常 ruleValue " + ruleValue + " 配置不正确");
        }

        //用户抽奖次数大于规则限定值，规则放行
        if (userRaffleCount >= raffleCount){
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                    .build();
        }


        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
