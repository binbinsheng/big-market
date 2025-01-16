package com.binbinsheng.infrastructure.persistent.po;

/*
抽奖策略
 */


import lombok.Data;

import java.util.Date;

@Data
public class Strategy {

    //自增ID
    private long id;
    //抽奖策略ID
    private long strategyId;
    //抽奖策略描述
    private String strategyDesc;

    //抽獎規則模型
    private String ruleModels;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime ;
}
