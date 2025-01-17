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

@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_BLACKLIST)
/*
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WIGHT) 是一个注解，
它用于给类或方法添加元数据，标记该类或方法使用了 RULE_BLACKLIST 这种逻辑策略。
这个注解可以让其他地方的代码识别和使用这个策略来执行相应的逻辑。
 */

public class RuleBackListLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    IStrategyRepository repository;  //查找数据库或redis

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {

        log.info("规则过滤-黑名单 userId:{} strategyId:{} ruleModel:{}",
                ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getRuleModel());
        String userId = ruleMatterEntity.getUserId();

        /* ruleValue = 100:user001,user002,user003 */
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getAwardId(),
                ruleMatterEntity.getRuleModel());

        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        //拿到黑名单只能抽的奖品ID
        Integer awardId = Integer.parseInt(splitRuleValue[0]);

        // 过滤其他规则
        /* userBlackIds = ["user001", "user002", "user003"] */
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            if (userId.equals(userBlackId)) {
                return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                        .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                        .data(RuleActionEntity.RaffleBeforeEntity.builder()
                                .strategyId(ruleMatterEntity.getStrategyId())
                                .awardId(awardId)
                                .build())
                        .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                        .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                        .build();
            }
        }

        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

}
