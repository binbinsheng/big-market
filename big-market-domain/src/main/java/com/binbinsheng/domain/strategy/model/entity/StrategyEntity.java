package com.binbinsheng.domain.strategy.model.entity;

/*
策略實體
 */

import com.binbinsheng.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StrategyEntity {

    //抽奖策略ID
    private long strategyId;
    //抽奖策略描述
    private String strategyDesc;

    //抽獎規則模型
    private String ruleModels;

    private String[] ruleModels(){
        if (StringUtils.isBlank(ruleModels)){
            return null;
        }
        return ruleModels.split(Constants.SPLIT);
    }

    public String getRuleWeight(){
        String[] ruleModels = this.ruleModels();
        for (String ruleModel : ruleModels){
            if ("rule_weight".equals(ruleModel)){
                return ruleModel;
            }
        }
        return null;
    }
}
