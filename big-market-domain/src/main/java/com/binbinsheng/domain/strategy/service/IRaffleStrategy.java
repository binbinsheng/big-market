package com.binbinsheng.domain.strategy.service;

/*
策略抽奖接口
 */


import com.binbinsheng.domain.strategy.model.entity.RaffleAwardEntity;
import com.binbinsheng.domain.strategy.model.entity.RaffleFactorEntity;

public interface IRaffleStrategy {

    RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);

}
