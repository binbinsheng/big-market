package com.binbinsheng.domain.strategy.service.rule.factory;

import com.binbinsheng.domain.strategy.model.entity.RuleActionEntity;
import com.binbinsheng.domain.strategy.service.annotation.LogicStrategy;
import com.binbinsheng.domain.strategy.service.rule.ILogicFilter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**

 规则工厂

 */
@Service
public class DefaultLogicFactory {

    public Map<String, ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    /*
    这段代码的目的是在 DefaultLogicFactory 的构造方法中将 logicFilters 列表中的元素（
    即实现了 ILogicFilter 接口的对象）根据其 LogicStrategy 注解的内容，添加到 logicFilterMap 中。
    具体来说，它是通过反射机制查找每个 ILogicFilter 对象上是否有 LogicStrategy 注解，
    并利用该注解的信息将 ILogicFilter 实例放入一个映射中。
     */
    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilters) {
        logicFilters.forEach(logic -> {
            LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
            if (null != strategy) {
                logicFilterMap.put(strategy.logicMode().getCode(), logic);
            }
        });
    }

    public <T extends RuleActionEntity.RaffleEntity> Map<String, ILogicFilter<T>> openLogicFilter() {
        return (Map<String, ILogicFilter<T>>) (Map<?, ?>) logicFilterMap;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WIGHT("rule_weight","【抽奖前规则】根据抽奖权重返回可抽奖范围KEY"),
        RULE_BLACKLIST("rule_blacklist","【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回"),

        ;

        private final String code;
        private final String info;

    }

}

