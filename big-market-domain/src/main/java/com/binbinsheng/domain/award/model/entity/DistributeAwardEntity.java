package com.binbinsheng.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 分发的奖品实体对象
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistributeAwardEntity {

    private String userId;

    private String orderId;

    private Integer awardId;

    private String awardConfig;

}
