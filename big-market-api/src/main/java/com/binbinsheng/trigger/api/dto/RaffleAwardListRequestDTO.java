package com.binbinsheng.trigger.api.dto;

import lombok.Data;

/**
 * 抽奖奖品列表请求对象
 */
@Data
public class RaffleAwardListRequestDTO {
    @Deprecated
    //策略Id
    private Long strategyId;
    //活动ID
    private Long activityId;
    //用户Id
    private String userId;
}
