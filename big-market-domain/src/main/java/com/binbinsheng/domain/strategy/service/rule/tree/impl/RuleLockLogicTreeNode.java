package com.binbinsheng.domain.strategy.service.rule.tree.impl;

import com.binbinsheng.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.binbinsheng.domain.strategy.repository.IStrategyRepository;
import com.binbinsheng.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.binbinsheng.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 次数锁结点
 */

@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    @Resource
    private IStrategyRepository repository;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue) {

        log.info("规则过滤-次数锁 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);

        long raffleCount = 0L;

        try {
            raffleCount  = Long.parseLong(ruleValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException("规则过滤-次数锁异常 ruleValue " + ruleValue + " 配置不正确");
        }


        //查询用户抽奖次数 -当天的:策略ID，活动ID 1:1的配置，可以直接用strategyId查询
        Integer userRaffleCount = repository.queryTodayUserRaffleCount(userId, strategyId);


        //用户抽奖次数大于规则限定值，规则放行
        if (userRaffleCount >= raffleCount){
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                    .build();
        }


        log.info("规则过滤-次数锁【拦截】 userId:{} strategyId:{} awardId:{} raffleCount:{} userRaffleCount:{}",
                userId, strategyId, awardId, userRaffleCount, userRaffleCount);

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
