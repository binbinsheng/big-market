package com.binbinsheng.domain.strategy.service.armory;

import com.binbinsheng.domain.strategy.model.entity.StrategyAwardEntity;
import com.binbinsheng.domain.strategy.model.entity.StrategyEntity;
import com.binbinsheng.domain.strategy.model.entity.StrategyRuleEntity;
import com.binbinsheng.domain.strategy.repository.IStrategyRepository;
import com.binbinsheng.types.common.Constants;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/*
策略装配库（兵工厂），负责初始化策略计算 service层
 */

@Slf4j
@Service
public class StrategyArmory implements IStrategyArmory, IStrategyDispatch {

    @Resource
    IStrategyRepository repository;

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        //1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);

        //2. 缓存奖品库存【用于decr扣减库存使用】
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            Integer awardId = strategyAwardEntity.getAwardId();
            Integer awardCount = strategyAwardEntity.getAwardCount();
            cacheStrategyAwardCount(strategyId, awardId, awardCount);
        }

        //3.1 默认装配配置【全量抽奖概率】
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwardEntities);

        //2.權重策略配置 - 適用於rule_weight 權重規則配置
        StrategyEntity strategyEntity  = repository.queryStrategyEntity(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (null == ruleWeight) {return true;}

        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRuleEntity(strategyId, ruleWeight);
        if (null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(),
                    ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }


        /* ruleWeightValueMap = {"4000:102,103,104,105": [102, 103, 104, 105],
                "5000:102,103,104,105,106,107": [102, 103, 104, 105, 106, 107],
                "6000:102,103,104,105,106,107,108,109": [102,103,104,105,106,107,108,109]}*/
        Map<String, List<Integer>> ruleWeightValueMap = strategyRuleEntity.getRuleWeightValues();
        Set<String> keys = ruleWeightValueMap.keySet();
        for (String key : keys) {
            /* ruleWeightValues = [102, 103, 104, 105] */
            List<Integer> ruleWeightValues = ruleWeightValueMap.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            /*entity 是一行记录*/
            strategyAwardEntitiesClone.removeIf(entity -> !ruleWeightValues.contains(entity.getAwardId()));

            /*此操作相当于在redis中装配了三张表："100001_4000:102,103,104,105" :
                                                        符合4000能抽的奖品表strategyAwardEntitiesClone*
                                             "100001_5000:102,103,104,105,106,107" : 5000表
                                             "100001_6000:6000:102,103,104,105,106,107,108,109" : 6000表/
             */
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key),
                    strategyAwardEntitiesClone);

        }

        return true;

    }

    private void cacheStrategyAwardCount(Long strategyId, Integer awardId, Integer awardCount) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId +Constants.UNDERLINE + awardId;
        repository.cacheStrategyAwardCount(cacheKey, awardCount);
    }

    private void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntities) {
        //1.获取最小概率值
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        //2.获取概率总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //3. 用1 / 0.0001获取概率范围
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        //4.
        ArrayList<Integer> strategyAwardSearchRateTable = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            for (int i = 0; i < rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchRateTable.add(awardId);
            }
        }

        //5.乱序
        Collections.shuffle(strategyAwardSearchRateTable);

        //6.存放到map里
        HashMap<Integer, Integer> shuffledStrategyAwardSearchRateTable = new HashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTable.size(); i++) {
            shuffledStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTable.get(i));
        }

        //8.存到Redis
        repository.storeStrategyAwardSearchRateTable(key, shuffledStrategyAwardSearchRateTable.size(), shuffledStrategyAwardSearchRateTable);
    }


    @Override
    public Integer getRandomAwardId(Long strategyId) {
        int rateRange = repository.getRateRange(strategyId);
        return repository.getStrategyAwardAssemble(String.valueOf(strategyId), new SecureRandom().nextInt(rateRange));
    }

    public Integer getRandomAwardId(Long strategyId, String ruleWeightValues) {
        /*key = 100001_4000:102,103,104,105 其实就是根据key找对应4000积分的表*/
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValues);
        int rateRange = repository.getRateRange(key);
        return repository.getStrategyAwardAssemble(key, new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Boolean subtractAwardStock(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId +Constants.UNDERLINE + awardId;
        return repository.subtractAwardStock(cacheKey);
    }
}
