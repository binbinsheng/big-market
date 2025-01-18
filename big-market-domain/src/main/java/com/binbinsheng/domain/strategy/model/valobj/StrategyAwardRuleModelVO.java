package com.binbinsheng.domain.strategy.model.valobj;

/*
抽奖策略规则规则值对象，就一个值(这个值里面可以包含多个值，如rule_models :rule_lock,rule_luck_award)
，所以不写成entity
 */

import com.binbinsheng.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.binbinsheng.types.common.Constants;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;


@Getter
@Builder
public class StrategyAwardRuleModelVO {

    private String ruleModels;

    public String[] raffleCenterRuleModelList(){
        ArrayList<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = ruleModels.split(Constants.SPLIT);
        for (String ruleModel : ruleModelValues) {
            if (DefaultLogicFactory.isCenter(ruleModel)){
                ruleModelList.add(ruleModel);
            }
        }
        return ruleModelList.toArray(new String[ruleModelList.size()]);
    }

}
