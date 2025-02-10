package com.binbinsheng.domain.rebate.service;

import com.binbinsheng.domain.rebate.model.entity.BehaviorEntity;
import com.binbinsheng.domain.rebate.model.entity.UserBehaviorRebateOrderEntity;
import com.binbinsheng.domain.rebate.model.valobj.BehaviorTypeVO;

import java.util.List;

/**
 * 行为返利服务接口
 */

public interface IBehaviorRebateService {

    /**创建返利订单 List接收可能存在多种行为，签到这个行为可能包括多种返利，比如加积分，比如加次数*/
    //返回的是一组订单编号
    List<String> createOrder(BehaviorEntity behaviorEntity);

    /**查询签到是否已经返利了，如果到库中查询得到，说明已经返利了*/
    List<UserBehaviorRebateOrderEntity> queryOrderByBusinessNo(String userId, String businessNo);

}
