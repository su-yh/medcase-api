package com.ruoyi.mvc.constants.enums;

import com.ruoyi.mvc.error.IErrorCode;

/**
 * 错误码枚举
 *
 * @author suyh
 * @since 2025-05-16
 */
public enum ErrorCodeEnums implements IErrorCode {
    ACCESS_DENIED(1000403, "禁止访问"),
    NO_IMPLEMENT(1000404, "功能代码还未实现"),
    SERVICE_ERROR(1000500, "服务错误"),
    SYSTEM_UNSUPPORTED(1000601, "Not Supported: {0}"),

    ;

    private final int code;
    private final String msg;

    ErrorCodeEnums(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
