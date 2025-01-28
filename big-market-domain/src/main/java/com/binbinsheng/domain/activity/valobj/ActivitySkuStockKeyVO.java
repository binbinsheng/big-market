package com.binbinsheng.domain.activity.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动sku库存key值对象
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySkuStockKeyVO {

    /**
     * 商品sku
     */
    private Long sku;

    /**
     * 活动Id
     */
    private Long activityId;

}
