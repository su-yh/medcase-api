package com.medcase.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 登录记录状态。
 */
@Getter
public enum LoginRecordStatusEnums implements BaseEnum {
    SUCCESS("0", "成功"),
    FAIL("1", "失败"),
    ;

    @JsonValue
    @EnumValue
    private final String code;
    private final String desc;

    LoginRecordStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
