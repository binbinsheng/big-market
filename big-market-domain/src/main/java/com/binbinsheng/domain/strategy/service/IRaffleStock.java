package com.binbinsheng.domain.strategy.service;

import com.binbinsheng.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

/**
 * 抽取库存相关服务，获取库存消耗队列
 */


public interface IRaffleStock {

    /**
     * 获取奖品库存消耗队列
     * return 奖品库存key信息
     *
     *      public class StrategyAwardStockKeyVO {
     *          private Long strategyId;
     *          private Integer awardId;
     *      }
     */
    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 更新奖品库存消耗记录
     * (更新数据库信息)
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);
}
