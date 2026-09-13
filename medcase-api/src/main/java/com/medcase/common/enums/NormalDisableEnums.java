package com.medcase.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 通用启用停用状态。
 */
@Getter
public enum NormalDisableEnums implements BaseEnum {
    NORMAL("0", "正常"),
    DISABLE("1", "停用");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    NormalDisableEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
