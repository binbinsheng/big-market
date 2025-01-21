package com.binbinsheng.domain.strategy.service.rule.chain.impl;

import com.binbinsheng.domain.strategy.repository.IStrategyRepository;
import com.binbinsheng.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.binbinsheng.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.binbinsheng.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 黑名单过滤规则责任结点
 */
@Slf4j
@Component("rule_blacklist")
public class BackListLogicChain extends AbstractLogicChain {

    @Resource
    private IStrategyRepository repository;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {

        log.info("抽奖责任链-黑名单开始 userId:{}, strategyId:{}, ruleModel:{}"
                , userId, strategyId, ruleModel());

        /* ruleValue = 100:user001,user002,user003 */
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());

        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        //拿到黑名单只能抽的奖品ID
        Integer awardId = Integer.parseInt(splitRuleValue[0]);

        // 过滤其他规则
        /* userBlackIds = ["user001", "user002", "user003"] */
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);

        for (String userBlackId : userBlackIds) {
            if (userId.equals(userBlackId)) {
                log.info("抽奖责任链-黑名单接管 userId: {} strategyId: {} ruleModel: {} awardId: {}"
                        , userId, strategyId, ruleModel(), awardId);
                return DefaultChainFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .logicModel(ruleModel())
                        .build();
            }
        }
        log.info("抽奖责任链-黑名单放行userId:{}, strategyId:{}, ruleModel:{}"
                , userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);

    }


    @Override
    public String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_BLACKLIST.getCode();
    }
}
