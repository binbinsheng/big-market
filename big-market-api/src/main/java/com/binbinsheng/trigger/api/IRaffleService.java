package com.binbinsheng.trigger.api;

import com.binbinsheng.trigger.api.dto.RaffleAwardListRequestDTO;
import com.binbinsheng.trigger.api.dto.RaffleAwardListResponseDTO;
import com.binbinsheng.trigger.api.dto.RaffleRequestDTO;
import com.binbinsheng.trigger.api.dto.RaffleResponseDTO;
import com.binbinsheng.types.model.Response;

import java.util.List;

/**
 * 抽奖服务接口
 */


public interface IRaffleService {

    /**
     * 策略装配接口
     * @param strategyId
     * @return
     */
    Response<Boolean> strategyArmory(Long strategyId);


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
    Response<RaffleResponseDTO> randomRaffle(RaffleRequestDTO requestDTO);
}
