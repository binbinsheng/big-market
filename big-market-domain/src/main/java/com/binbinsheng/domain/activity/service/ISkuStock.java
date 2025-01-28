package com.binbinsheng.domain.activity.service;

import com.binbinsheng.domain.activity.valobj.ActivitySkuStockKeyVO;

public interface ISkuStock {

    /**
     * 获取活动sku库存消耗队列，从队列中拿出具体的sku订单
     * @return sku库存key信息
     */
    ActivitySkuStockKeyVO takeQueueValue();


    /**
     * 拿到MQ清空的message后，清空队列
     */
    void clearQueueValue();

    /**
     * 延迟队列 + 任务趋势更新活动sku库存 ，搭配takeQueueValue()
     * @param sku
     */
    void updateActivitySkuStock(Long sku);

    /**
     * 缓存库存已消耗完毕，提示清空数据库库存，搭配clearActivitySkuStock()
     * @param sku
     */
    void clearActivitySkuStock(Long sku);
}
