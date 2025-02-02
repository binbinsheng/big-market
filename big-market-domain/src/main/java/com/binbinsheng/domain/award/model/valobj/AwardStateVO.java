package com.binbinsheng.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

@Getter
@AllArgsConstructor
public enum AwardStateVO {

    create("create", "创建"),
    complete("complete", "发奖完成"),
    fail("fail", "发奖失败")
    ;

    private final String code;
    private final String desc;

}
