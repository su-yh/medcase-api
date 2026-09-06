package com.medcase.mvc.audit;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author suyh
 * @since 2026-09-06
 */
public enum AuditEnums implements IAudit {
    CREATE_ADMIN_USER("create_admin_user", "创建后台用户"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    AuditEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }
}
