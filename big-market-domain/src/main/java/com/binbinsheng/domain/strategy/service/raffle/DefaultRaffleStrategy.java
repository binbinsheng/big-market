package com.binbinsheng.domain.strategy.service.raffle;


import com.binbinsheng.domain.strategy.model.entity.StrategyAwardEntity;
import com.binbinsheng.domain.strategy.model.valobj.RuleTreeVO;
import com.binbinsheng.domain.strategy.model.valobj.RuleWeightVO;
import com.binbinsheng.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.binbinsheng.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.binbinsheng.domain.strategy.repository.IStrategyRepository;
import com.binbinsheng.domain.strategy.service.AbstractRaffleStrategy;
import com.binbinsheng.domain.strategy.service.IRaffleAward;
import com.binbinsheng.domain.strategy.service.IRaffleStock;
import com.binbinsheng.domain.strategy.service.armory.IStrategyDispatch;
import com.binbinsheng.domain.strategy.service.rule.IRaffleRule;
import com.binbinsheng.domain.strategy.service.rule.chain.ILogicChain;
import com.binbinsheng.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.binbinsheng.domain.strategy.service.rule.tree.engin.IDecisionTreeEngine;
import com.binbinsheng.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleAward, IRaffleStock, IRaffleRule {



    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory,
                                 DefaultTreeFactory defaultTreeFactory) {
        super(repository, strategyDispatch, defaultChainFactory, defaultTreeFactory);
    }

    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
        return logicChain.logic(userId, strategyId);
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        return raffleLogicTree(userId, strategyId, awardId, null);
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId, Date endDate) {
        //查看看这个奖品是否配置rule_model,若无配置rule_model,则无需经过规则树
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModel(strategyId, awardId);
        if (strategyAwardRuleModelVO == null) {
            return DefaultTreeFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .build();
        }

        RuleTreeVO ruleTreeVO = repository.
                queryRuleTreeVOByTreeId(strategyAwardRuleModelVO.getRuleModels());

        //没有配置规则树
        if (ruleTreeVO == null){
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，" +
                    "未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 "
                    + strategyAwardRuleModelVO.getRuleModels());
        }

        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        DefaultTreeFactory.StrategyAwardVO strategyAwardVO = treeEngine.process(userId, strategyId, awardId, endDate);

        return strategyAwardVO;
    }


    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        return repository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        repository.updateStrategyAwardStock(strategyId, awardId);
    }

    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId) {
        return repository.queryStrategyAwardList(strategyId);
    }

    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardListByActivityId(Long activityId) {
        Long strategyId = repository.queryStrategyIdByActivityId(activityId);
        return queryRaffleStrategyAwardList(strategyId);
    }

    @Override
    public Map<String, Integer> queryAwardRuleLockCount(String[] treeIds) {
        return repository.queryAwardRuleLockCount(treeIds);
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeight(Long strategyId) {
        return repository.queryAwardRuleWeight(strategyId);
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeightByActivityId(Long activityId) {
        Long strategyId = repository.queryStrategyIdByActivityId(activityId);
        return queryAwardRuleWeight(strategyId);
    }
}
