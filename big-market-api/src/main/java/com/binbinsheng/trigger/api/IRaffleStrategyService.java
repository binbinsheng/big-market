package com.binbinsheng.trigger.api;

import com.binbinsheng.trigger.api.dto.*;
import com.binbinsheng.types.model.Response;

import java.util.List;

/**
 * 抽奖服务接口
 */


public interface IRaffleStrategyService {

    /**
     * 策略装配接口
     * @param strategyId
     * @return
     */
    Response<Boolean> strategyArmory(Long strategyId);


    /**
     * 查询抽奖策略规则
     * （抽多少次必得某个奖品->都是数据库配置的）
     * @param requestDTO
     * @return
     */
    Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightRequestDTO requestDTO);

    /**
     * 查询抽奖奖品列表配置
     * @param requestDTO
     * @return
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);

    /**
     * 随机抽奖接口
     * @param requestDTO
     * @return
     */
    Response<RaffleStrategyResponseDTO> randomRaffle(RaffleStrategyRequestDTO requestDTO);
}
