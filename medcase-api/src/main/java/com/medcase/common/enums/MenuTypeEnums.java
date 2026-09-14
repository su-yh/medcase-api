package com.medcase.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 菜单类型。
 */
@Getter
public enum MenuTypeEnums implements BaseEnum {
    DIRECTORY("M", "目录"),
    MENU("C", "菜单"),
    BUTTON("F", "按钮");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    MenuTypeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
