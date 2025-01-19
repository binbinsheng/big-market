package com.binbinsheng.domain.strategy.service.rule.chain.factory;

import com.binbinsheng.domain.strategy.model.entity.StrategyEntity;
import com.binbinsheng.domain.strategy.repository.IStrategyRepository;
import com.binbinsheng.domain.strategy.service.rule.chain.ILogicChain;
import com.binbinsheng.types.common.Constants;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultChainFactory {

    private final Map<String, ILogicChain> logicChainMap;

    private final IStrategyRepository repository;


    public DefaultChainFactory(Map<String, ILogicChain> logicChainMap, IStrategyRepository repository) {
        this.logicChainMap = logicChainMap;
        this.repository = repository;
    }

    public ILogicChain openLogicChain(Long strategyId){
        StrategyEntity strategy = repository.queryStrategyEntity(strategyId);
        //ruleModels = ["rule_weight", "rule_blacklist"]
        String[] ruleModels = strategy.ruleModels();

        //如果没有设置规则，则返回default
        if (ruleModels == null || ruleModels.length == 0){
            return logicChainMap.get("default");
        }

        //拿到责任链的第一个结点
        ILogicChain logicChain = logicChainMap.get(ruleModels[0]);
        ILogicChain current = logicChain;
        for (int i = 1; i < ruleModels.length; i++){
            ILogicChain nextChainNode = logicChain.appendNext(logicChainMap.get(ruleModels[i]));
            current = nextChainNode;
        }

        current.appendNext(logicChainMap.get("default"));

        return logicChain;//有点像返回链表的头节点



    }


}
