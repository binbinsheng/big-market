package com.binbinsheng.domain.strategy.service;

import com.binbinsheng.domain.strategy.model.entity.RaffleAwardEntity;
import com.binbinsheng.domain.strategy.model.entity.RaffleFactorEntity;
import com.binbinsheng.domain.strategy.model.entity.RuleActionEntity;
import com.binbinsheng.domain.strategy.model.entity.StrategyAwardEntity;
import com.binbinsheng.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.binbinsheng.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.binbinsheng.domain.strategy.repository.IStrategyRepository;
import com.binbinsheng.domain.strategy.service.armory.IStrategyDispatch;
import com.binbinsheng.domain.strategy.service.rule.chain.ILogicChain;
import com.binbinsheng.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.binbinsheng.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.binbinsheng.types.enums.ResponseCode;
import com.binbinsheng.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    // 策略仓储服务 -> domain层像一个大厨，仓储层提供米面粮油
    protected IStrategyRepository repository;
    // 策略调度服务 -> 只负责抽奖处理，通过新增接口的方式，隔离职责，不需要使用方关心或者调用抽奖的初始化
    protected IStrategyDispatch strategyDispatch;

    protected DefaultChainFactory defaultChainFactory;

    protected DefaultTreeFactory defaultTreeFactory;

    public AbstractRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch,
                                  DefaultChainFactory defaultChainFactory,
                                  DefaultTreeFactory defaultTreeFactory) {
        this.repository = repository;
        this.strategyDispatch = strategyDispatch;
        this.defaultChainFactory = defaultChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {

        // 1. 参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (null == strategyId || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

       //2.责任链抽奖计算【这步拿到的是初步的抽奖ID，之后需要根据ID处理抽奖】注意：黑名单、权重等非默认抽奖的直接返回抽奖结果
        DefaultChainFactory.StrategyAwardVO chainStrategyAwardVO = raffleLogicChain(userId, strategyId);
        log.info("抽奖策略计算-责任链{} {} {} {}", userId, strategyId, chainStrategyAwardVO.getAwardId(),
                chainStrategyAwardVO.getLogicModel());
        if (!DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(chainStrategyAwardVO.getLogicModel())){
            // TODO awardConfig 暂时为空。黑名单指定积分奖品，后续需要在库表中配置上对应的1积分值，并获取到。
            return buildRaffleAwardEntity(strategyId, chainStrategyAwardVO.getAwardId(), null);

        }

        //3.规则树抽奖过滤（若是Default，则走这）【奖品ID，会根据抽奖次数判断、库存判断、兜底返回最终的可获得奖品信息】
        DefaultTreeFactory.StrategyAwardVO treeStrategyAwardVO = raffleLogicTree(userId, strategyId, chainStrategyAwardVO.getAwardId(), raffleFactorEntity.getEndDateTime());
        log.info("抽奖策略计算-规则树{} {} {} {}", userId, strategyId, treeStrategyAwardVO.getAwardId(),
                treeStrategyAwardVO.getAwardRuleValue());

        // 4. 返回抽奖结果
        return buildRaffleAwardEntity(strategyId, treeStrategyAwardVO.getAwardId(), treeStrategyAwardVO.getAwardRuleValue());


    }

    private RaffleAwardEntity buildRaffleAwardEntity(Long strategyId, Integer awardId, String awardConfig) {
        StrategyAwardEntity strategyAward = repository.queryStrategyAwardEntity(strategyId, awardId);
        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .awardConfig(awardConfig)
                .awardTitle(strategyAward.getAwardTitle())
                .sort(strategyAward.getSort())
                .build();
    }



    /**
     * 抽奖计算，责任链抽象方法
     * @param userId
     * @param strategyId
     * @return 奖品Id
     */
    public abstract DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId);

    /**
     * 抽奖结果过滤，决策树抽象方法
     * @param userId
     * @param strategyId
     * @param awardId
     * @return 过两次结果【奖品ID，会根据抽奖次数判断，库存判断，兜底返回最终的可获得奖品信息】
     */
    public abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId);

    /**
     * 抽奖结果过滤，决策树抽象方法
     * @param userId
     * @param strategyId
     * @param awardId
     * @param endDate 活动结束时间
     * @return 过两次结果【奖品ID，会根据抽奖次数判断，库存判断，兜底返回最终的可获得奖品信息】
     */
    public abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId, Date endDate);







}
