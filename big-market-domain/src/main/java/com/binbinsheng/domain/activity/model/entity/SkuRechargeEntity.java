package com.binbinsheng.domain.activity.model.entity;

import lombok.Data;

/**
 * 活动充值实体对象
 */
@Data
public class SkuRechargeEntity {

    /**用户Id**/
    private String userId;

    /**商品sku - activity + activity count */
    private Long sku;

    /**多次调用也能确保结果唯一，不会多次充值*/
    private String outBusinessNo;

}
