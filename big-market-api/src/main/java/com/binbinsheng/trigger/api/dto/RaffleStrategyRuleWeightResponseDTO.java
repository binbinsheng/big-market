package com.binbinsheng.trigger.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class RaffleStrategyRuleWeightResponseDTO {

    //这里是想返回抽奖下面那个进度条，抽几次必得奖品的那个规则配置（60次必得奖品）
    private Integer ruleWeightCount;

    //用户完成了几次抽奖
    private Integer userActivityAccountTotalUseCount;

    //返回在该抽奖次数下的奖品配置
    private List<StrategyAward> strategyAwards;

    @Data
    public static class StrategyAward{
        private Integer awardId;
        private String awardTitle;
    }

}
