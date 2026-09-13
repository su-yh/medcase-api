package com.medcase.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 是否选项。
 */
@Getter
public enum YesNoEnums implements BaseEnum {
    YES("Y", "是"),
    NO("N", "否");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    YesNoEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
