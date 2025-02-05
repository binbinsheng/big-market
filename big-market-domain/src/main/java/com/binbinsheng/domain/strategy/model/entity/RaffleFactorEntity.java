package com.binbinsheng.domain.strategy.model.entity;

/*
抽奖因子实体
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleFactorEntity {

    private String userId;;
    private Long strategyId;
    /** 结束时间 */
    private Date endDateTime;

}
