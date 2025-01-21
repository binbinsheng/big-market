package com.binbinsheng.domain.strategy.service.rule.chain.impl;

import com.binbinsheng.domain.strategy.repository.IStrategyRepository;
import com.binbinsheng.domain.strategy.service.armory.IStrategyDispatch;
import com.binbinsheng.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.binbinsheng.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.binbinsheng.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 权重过滤规则责任结点
 */

@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Resource
    IStrategyRepository repository;

    @Resource
    IStrategyDispatch strategyDispatch;

    //用户积分，先写死
    Long userScore = 0L;


    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重开始 userId: {} strategyId: {} ruleModel: {}",
                userId, strategyId, ruleModel());

        /*
        4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
         */
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());

        //1.根据用户ID查询用户抽奖消耗的积分值，本章先写死为固定的值，后续需要从数据库中查询
        /* analyticalValueGroup = {4000: "4000:102,103,104,105",
                                5000: "5000:102,103,104,105,106,107",
                                6000: "102,103,104,105,106,107,108,109"} */
        Map<Long, String> analyticalValueGroup = getAnalytical(ruleValue);
        if (analyticalValueGroup == null || analyticalValueGroup.isEmpty()) {
            return null;
        }

        //2.转换Keys值，并默认排序
        /* analyticalSortedKeys = [4000, 5000, 6000] */
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);

        //3.如4500积分找到4000的key， 5500找到5000的key
        Long nextValue = analyticalSortedKeys.stream().filter(key ->
                        userScore >= key)
                .sorted(Comparator.reverseOrder())
                .findFirst()
                .orElse(null);

        if (nextValue != null) {
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId,
                    analyticalValueGroup.get(nextValue));
            log.info("抽奖责任链-权重接管 userId:{}, strategyId:{}, ruleModel:{}, awardId:{}"
                    , userId, strategyId, ruleModel(),awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .logicModel(ruleModel())
                    .build();
        }


        log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel: {}",
                userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }



    @Override
    public String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode();
    }


    /*
        将4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
        变成{4000: "4000:102,103,104,105", 5000: "5000:102,103,104,105,106,107",
        6000: "102,103,104,105,106,107,108,109"}
    */
    private Map<Long, String> getAnalytical(String ruleValue){
        Map<Long, String> ruleValueMap = new HashMap<>();
        String[] ruleValuesGroup = ruleValue.split(Constants.SPACE);
        for (String ruleValueKey : ruleValuesGroup) {
            //检查输入是否为空
            if (ruleValueKey == null || ruleValueKey.isEmpty()) {
                return ruleValueMap;
            }

            String[] parts = ruleValueKey.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format"
                        + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
        }
        return ruleValueMap;

    }
}
