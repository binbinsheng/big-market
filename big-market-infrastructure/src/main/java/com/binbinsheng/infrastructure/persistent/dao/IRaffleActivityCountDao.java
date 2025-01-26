package com.binbinsheng.infrastructure.persistent.dao;

import com.binbinsheng.infrastructure.persistent.po.RaffleActivityCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抽奖活动次数配置表
 */


@Mapper
public interface IRaffleActivityCountDao {

    RaffleActivityCount queryRaffleActivityCountByActivityCountId(Long activityCountId);



}
