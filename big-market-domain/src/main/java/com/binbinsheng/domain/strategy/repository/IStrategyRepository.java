package com.binbinsheng.domain.strategy.repository;

/*
策略仓储接口
 */

import com.binbinsheng.domain.strategy.model.entity.StrategyAwardEntity;
import com.binbinsheng.domain.strategy.model.entity.StrategyEntity;
import com.binbinsheng.domain.strategy.model.entity.StrategyRuleEntity;
import com.binbinsheng.domain.strategy.model.valobj.RuleTreeVO;
import com.binbinsheng.domain.strategy.model.valobj.RuleWeightVO;
import com.binbinsheng.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.binbinsheng.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(String key, Integer rateRange, Map<Integer, Integer> shuffledStrategyAwardSearchRateTable);

    int getRateRange(Long strategyId);

    int getRateRange(String key);

    Integer getStrategyAwardAssemble(String key, Integer rateKey);

    StrategyEntity queryStrategyEntity(Long strategyId);

    StrategyRuleEntity queryStrategyRuleEntity(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, Integer awardId ,String ruleModel);

    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModel(Long strategyId, Integer awardId);

    RuleTreeVO queryRuleTreeVOByTreeId(String treeId);

    void cacheStrategyAwardCount(String cacheKey, Integer awardCount);

    Boolean subtractAwardStock(String cacheKey);

    Boolean subtractAwardStock(String cacheKey, Date endDate);

    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO);

    StrategyAwardStockKeyVO takeQueueValue();

    void updateStrategyAwardStock(Long strategyId, Integer awardId);

    /**
     * 根据策略ID+奖品ID的唯一组合，查询奖品信息
     * @param strategyId
     * @param awardId
     * @return
     */
    StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId);

    Long queryStrategyIdByActivityId(Long activityId);

    Integer queryTodayUserRaffleCount(String userId, Long strategyId);

    Map<String, Integer> queryAwardRuleLockCount(String[] treeIds);

    Integer queryActivityAccountTotalUseCount(String userId, Long strategyId);

    List<RuleWeightVO> queryAwardRuleWeight(Long strategyId);
}
