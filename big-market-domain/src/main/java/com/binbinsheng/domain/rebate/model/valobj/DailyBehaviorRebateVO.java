package com.binbinsheng.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 日常行为返利值对象（不对数据库产生影响，定义为值对象）
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DailyBehaviorRebateVO {

    /** 行为类型（sign 签到、openai_pay 支付） */
    private String behaviorType;
    /** 返利描述 */
    private String rebateDesc;
    /** 返利类型（sku 活动库存充值商品、integral 用户活动积分） */
    private String rebateType;
    /** 返利配置 */
    private String rebateConfig;

}
