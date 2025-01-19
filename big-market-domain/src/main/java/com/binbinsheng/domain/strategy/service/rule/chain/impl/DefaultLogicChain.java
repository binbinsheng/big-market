package com.binbinsheng.domain.strategy.service.rule.chain.impl;

import com.binbinsheng.domain.strategy.service.armory.IStrategyDispatch;
import com.binbinsheng.domain.strategy.service.rule.chain.AbstractLogicChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 兜底方法结点
 */

@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicChain {

    @Resource
    IStrategyDispatch strategyDispatch;

    @Override
    public Integer logic(String userId, Long strategyId) {
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId:{}, strategyId:{}, ruleModel:{}, awardId:{}"
                , userId, strategyId, ruleModel(), awardId);
        return awardId;
    }


    @Override
    public String ruleModel() {
        return "default";
    }
}
