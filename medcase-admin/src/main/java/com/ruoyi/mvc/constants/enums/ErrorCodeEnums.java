package com.ruoyi.mvc.constants.enums;

import com.ruoyi.mvc.error.IErrorCode;

/**
 * 错误码枚举
 *
 * @author suyh
 * @since 2025-05-16
 */
public enum ErrorCodeEnums implements IErrorCode {
    USER_NOT_LOGIN(1000401, "用户未登录"),
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
    DOCTOR_UPDATE_REJECT(2000013, "只允许修改自己的病例"),
    DOCTOR_UPDATE_STATUS_NOT_MATCH(2000014, "只允许修改草稿病例"),
    USER_TYPE_NOT_MATCH(2000015, "禁止访问，用户类型错误"),
    DOCTOR_DELETE_STATUS_NOT_MATCH(2000016, "只允许删除草稿病例"),
    DOCTOR_CASE_DELETE_FAILED(2000017, "病例删除失败，请稍后重试"),
    DOCTOR_CASE_REVIEW_STATUS_NOT_MATCH(2000018, "只允许审核待审核病例"),
    DOCTOR_CASE_REVIEW_REASON_EMPTY(2000019, "审核拒绝原因不能为空"),
    DOCTOR_CASE_REVIEW_FAILED(2000020, "病例审核失败，请稍后重试"),
    DOCTOR_CASE_REVIEW_STATUS_INVALID(2000021, "审核状态无效"),
    DOCTOR_CASE_SETTLE_STATUS_NOT_MATCH(2000022, "只允许结算审核通过的待结算病例"),
    DOCTOR_CASE_SETTLE_FAILED(2000023, "病例结算失败，请稍后重试"),
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
