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
    DOCTOR_REGISTER_USERNAME_EMPTY(2000001, "医生注册用户名不能为空"),
    DOCTOR_REGISTER_PASSWORD_EMPTY(2000002, "医生注册密码不能为空"),
    DOCTOR_REGISTER_USERNAME_LENGTH_INVALID(2000003, "医生注册用户名长度必须在2到20个字符之间"),
    DOCTOR_REGISTER_PASSWORD_LENGTH_INVALID(2000004, "医生注册密码长度必须在5到20个字符之间"),
    DOCTOR_REGISTER_USER_EXISTS(2000005, "保存用户'{0}'失败，注册账号已存在"),
    DOCTOR_REGISTER_FAILED(2000006, "医生注册失败，请联系系统管理人员"),
    DOCTOR_LOGIN_USER_NOT_EXISTS(2000007, "医生账号不存在"),
    DOCTOR_CASE_NOT_FOUND(2000009, "病例不存在"),
    DOCTOR_CASE_SUBMIT_FAILED(2000010, "病例提交失败，请稍后重试"),
    DOCTOR_LOGIN_FAILED(2000012, "医生登录失败，请检查账号和密码"),

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
