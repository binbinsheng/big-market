package com.binbinsheng.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 给用户的账户加积分
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreditAwardEntity {
    private String userId;
    private BigDecimal creditAmount;

}
