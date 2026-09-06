package com.medcase.mvc.audit;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author suyh
 * @since 2026-09-06
 */
public enum AuditEnums implements IAudit {
    ;

    @EnumValue
    private final String operation;

    AuditEnums(String operation) {
        this.operation = operation;
    }

    @Override
    public String getOperation() {
        return operation;
    }
}
