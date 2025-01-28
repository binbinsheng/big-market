package com.binbinsheng.domain.activity.service.armory;

import java.util.Date;

/**
 * 活动调度【扣减缓存】
 */

public interface IActivityDispatch {

    /**
     * 根据sku扣减库存
     * @param sku
     * @param endDateTime
     * @return
     */
    boolean subtractionActivitySkuStock(Long sku, Date endDateTime);
}
