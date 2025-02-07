package com.binbinsheng.domain.rebate.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行为订单返利实体对象
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserBehaviorRebateOrderEntity {
    /** 用户ID */
    private String userId;
    /** 订单ID */
    private String orderId;
    /** 行为类型（sign 签到、openai_pay 支付） */
    private String behaviorType;
    /** 返利描述 */
    private String rebateDesc;
    /** 返利类型（sku 活动库存充值商品、integral 用户活动积分） */
    private String rebateType;
    /** 返利配置【sku值，积分值】 */
    private String rebateConfig;
    /** 业务ID - 拼接的唯一值 */
    private String bizId;
}
