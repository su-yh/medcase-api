package com.medcase.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 公告状态。
 */
@Getter
public enum NoticeStatusEnums implements BaseEnum {
    NORMAL("0", "正常"),
    CLOSED("1", "关闭");

    @EnumValue
    @JsonValue
    private final String code;

    private final String desc;

    NoticeStatusEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
