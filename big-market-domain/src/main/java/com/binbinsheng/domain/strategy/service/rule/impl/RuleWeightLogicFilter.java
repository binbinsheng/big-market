package com.binbinsheng.domain.strategy.service.rule.impl;

import com.binbinsheng.domain.strategy.model.entity.RuleActionEntity;
import com.binbinsheng.domain.strategy.model.entity.RuleMatterEntity;
import com.binbinsheng.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.binbinsheng.domain.strategy.repository.IStrategyRepository;
import com.binbinsheng.domain.strategy.service.annotation.LogicStrategy;
import com.binbinsheng.domain.strategy.service.rule.ILogicFilter;
import com.binbinsheng.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.binbinsheng.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    IStrategyRepository repository;
    
    //用户积分，先写死
    Long userScore = 4500L;
    
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {

        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}",
                ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();

        /*
        4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
         */
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(),
                ruleMatterEntity.getRuleModel());

        //1.根据用户ID查询用户抽奖消耗的积分值，本章先写死为固定的值，后续需要从数据库中查询
        /* analyticalValueGroup = {4000: "4000:102,103,104,105",
                                5000: "5000:102,103,104,105,106,107",
                                6000: "102,103,104,105,106,107,108,109"} */
        Map<Long, String> analyticalValueGroup = getAnalytical(ruleValue);
        if (analyticalValueGroup == null || analyticalValueGroup.isEmpty()) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        //2.转换Keys值，并默认排序
        /* analyticalSortedKeys = [4000, 5000, 6000] */
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);

        //3.如4500积分找到4000的key， 5500找到5000的key
        Long nextValue = analyticalSortedKeys.stream().filter(key ->
                        userScore >= key)
                .findFirst()
                .orElse(null);

        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .data(RuleActionEntity.RaffleBeforeEntity.builder()
                        .strategyId(strategyId)
                        .ruleWeightValueKey(analyticalValueGroup.get(nextValue))
                        .build())
                .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .build();


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
