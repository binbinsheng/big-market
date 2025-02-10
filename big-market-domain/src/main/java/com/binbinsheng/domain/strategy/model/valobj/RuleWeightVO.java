package com.binbinsheng.domain.strategy.model.valobj;


import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RuleWeightVO {

    /**
     * 60:102
     * 4000:102,103,104,105
     * 5000:102,103,104,105,106,107
     * 6000:102,103,104,105,106,107,108 */
    private String ruleValue;

    /** 60 */
    private Integer weight;

    /** [102] */
    private List<Integer>  awardIds;

    /** 102的具体奖品配置 */
    private List<Award> awardList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Award{
        private Integer awardId;
        private String awardTitle;
    }


}
