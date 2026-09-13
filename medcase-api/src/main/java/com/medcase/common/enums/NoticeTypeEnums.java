package com.medcase.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 公告类型。
 */
@Getter
public enum NoticeTypeEnums implements BaseEnum {
    NOTICE("1", "通知"),
    ANNOUNCEMENT("2", "公告");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    NoticeTypeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
