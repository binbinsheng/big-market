package com.binbinsheng.domain.activity.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户抽奖订单状态枚举
 * @create 2024-04-04 18:55
 */
@Getter
@AllArgsConstructor
public enum UserRaffleOrderStateVO {

    create("create", "创建"),
    used("used", "已使用"),
    cancel("cancel", "已作废"),
    ;

    private final String code;
    private final String desc;

}
