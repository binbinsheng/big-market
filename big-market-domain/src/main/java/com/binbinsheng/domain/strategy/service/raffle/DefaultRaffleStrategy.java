package com.binbinsheng.domain.strategy.service.raffle;

import com.binbinsheng.domain.strategy.model.entity.RaffleFactorEntity;
import com.binbinsheng.domain.strategy.model.entity.RuleActionEntity;
import com.binbinsheng.domain.strategy.model.entity.RuleMatterEntity;
import com.binbinsheng.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.binbinsheng.domain.strategy.repository.IStrategyRepository;
import com.binbinsheng.domain.strategy.service.armory.IStrategyDispatch;
import com.binbinsheng.domain.strategy.service.rule.ILogicFilter;
import com.binbinsheng.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.sun.deploy.cache.CacheEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy{

    @Resource
    private DefaultLogicFactory logicFactory;


    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch) {
        super(repository, strategyDispatch);
    }

    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity>
    doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
        /*                                                          //↑ rule_weight,rule_blacklist
            raffleFactorEntity ={
                            private String userId;
                            private Long strategyId;}
         */

        /*
        logicFilterGroup = {rule_weight: RuleWeightLogicFilter,
                            rule_blacklist: RuleBackListLogicFilter}
         */
        Map<String, ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> logicFilterGroup
                = logicFactory.openLogicFilter();

        // 黑名单规则优先过滤
        //找到"rule_blacklist"
        String ruleBackList = Arrays.stream(logics)
                .filter(str -> str.contains(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);

        //存在rule_blacklist这个字段
        if (StringUtils.isNotBlank(ruleBackList)) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter
                    = logicFilterGroup.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setAwardId(ruleMatterEntity.getAwardId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity
                    = logicFilter.filter(ruleMatterEntity);
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) {
                return ruleActionEntity;
            }
        }

        // 顺序过滤剩余规则
        List<String> ruleList = Arrays.stream(logics)
                .filter(s -> !s.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());

        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = null;
        for (String ruleModel : ruleList) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterGroup.get(ruleModel);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setAwardId(ruleMatterEntity.getAwardId());
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(ruleModel);
            ruleActionEntity = logicFilter.filter(ruleMatterEntity);
            // 非放行结果则顺序过滤
            log.info("抽奖前规则过滤 userId: {} ruleModel: {} code: {} info: {}",
                    raffleFactorEntity.getUserId(), ruleModel,
                    ruleActionEntity.getCode(), ruleActionEntity.getInfo());
            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(ruleActionEntity.getCode())) return ruleActionEntity;
        }

        return ruleActionEntity;
    }

}
