package com.binbinsheng.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 * 用户状态枚举，开启open还是冻结close
 */

@AllArgsConstructor
@Getter
public enum AccountStatusVO {
    open("open","开启"),
    close("close","冻结");

    private final String code;
    private final String desc;
}
