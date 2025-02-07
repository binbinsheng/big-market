package com.binbinsheng.domain.rebate.model.entity;

import com.binbinsheng.domain.rebate.model.valobj.BehaviorTypeVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 行为实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorEntity {

    private String userId;

    /** 行为类型：签到？充值？...*/
    private BehaviorTypeVO behaviorType;

    /** 唯一订单Id，保证幂等*/
    private String outBusinessNo;

}
