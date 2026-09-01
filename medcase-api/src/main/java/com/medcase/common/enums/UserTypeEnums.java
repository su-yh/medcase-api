package com.medcase.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author suyh
 * @since 2026-08-19
 */
@Getter
public enum UserTypeEnums implements BaseEnum {
    ADMIN("00", "后台用户"),
    DOCTOR("01", "医生"),
    PATIENT("02", "患者"),
    ;

    @EnumValue
    @JsonValue
    private final String code;
    private final String desc;

    UserTypeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", desc, code);
    }

    public static UserTypeEnums fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (UserTypeEnums userType : values()) {
            if (userType.code.equals(code)) {
                return userType;
            }
        }
        return null;
    }
}
