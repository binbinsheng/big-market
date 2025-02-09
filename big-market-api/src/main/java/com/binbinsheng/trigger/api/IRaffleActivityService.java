package com.binbinsheng.trigger.api;

import com.binbinsheng.trigger.api.dto.ActivityDrawRequestDTO;
import com.binbinsheng.trigger.api.dto.ActivityDrawResponseDTO;
import com.binbinsheng.types.model.Response;

/**
 * 抽奖活动服务
 *
 * (这里用活动包装，参与活动 之后再走抽奖算法策略。之前的只是算法策略)
 */

public interface IRaffleActivityService {

    /**
     * 活动装配，数据预热缓存
     * @param activityId
     * @return
     */
    Response<Boolean> armory(Long activityId);

    /**
     * 活动抽奖接口
     * @param request 请求对象
     * @return 返回结果
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO request);


    /**
     * 日历签到返利接口
     * @param userId
     * @return
     */
    Response<Boolean> calendarSignRebate(String userId);
}
