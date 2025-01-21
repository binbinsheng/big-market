package com.binbinsheng.domain.strategy.model.valobj;

/*
抽奖策略规则规则值对象，就一个值(这个值里面可以包含多个值，如rule_models :rule_lock,rule_luck_award)
，所以不写成entity
 */


import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;


@Getter
@Builder
public class StrategyAwardRuleModelVO {

    private String ruleModels;

}
