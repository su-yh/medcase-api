package com.ruoyi.common.enums;

import lombok.Getter;

/**
 * @author suyh
 * @since 2026-08-23
 */
@Getter
public enum UserStatusEnums implements BaseEnum {
    OK("0", "正常"),
    DISABLE("1", "停用"),
    DELETED("2", "删除"),
    PENDING_REVIEW("3", "待审核"),
    ;

    private final String code;
    private final String desc;

    UserStatusEnums(String code, String desc)
    {
        this.code = code;
        this.desc = desc;
    }

}
