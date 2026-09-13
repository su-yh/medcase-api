package com.medcase.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户性别。
 */
@Getter
public enum UserSexEnums implements BaseEnum {
    MALE("0", "男"),
    FEMALE("1", "女"),
    UNKNOWN("2", "未知");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    UserSexEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
