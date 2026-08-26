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
    RATE_LIMIT_EXCEEDED(1000602, "访问过于频繁，请稍候再试"),
    USER_DELETED(1000603, "对不起，您的账号已被删除"),
    USER_BLOCKED(1000604, "用户已封禁，请联系管理员"),
    USER_ID_RESOLVE_FAILED(1000605, "获取用户ID异常"),
    DEPT_ID_RESOLVE_FAILED(1000606, "获取部门ID异常"),
    USERNAME_RESOLVE_FAILED(1000607, "获取用户账户异常"),
    LOGIN_USER_RESOLVE_FAILED(1000608, "获取用户信息异常"),
    SUPER_ADMIN_USER_OPERATION(1000609, "不允许操作超级管理员用户"),
    USER_DATA_SCOPE_DENIED(1000610, "没有权限访问用户数据！"),
    USER_IMPORT_EMPTY(1000611, "导入用户数据不能为空！"),
    USER_IMPORT_FAILED(1000612, "{0}"),
    CONFIG_BUILTIN_DELETE(1000613, "内置参数【{0}】不能删除 "),
    MENU_SORT_SAVE_FAILED(1000614, "保存排序异常，请联系管理员"),
    DEPT_DATA_SCOPE_DENIED(1000615, "没有权限访问部门数据！"),
    DEPT_DISABLED(1000616, "部门停用，不允许新增"),
    DEPT_SORT_SAVE_FAILED(1000617, "保存排序异常，请联系管理员"),
    SUPER_ADMIN_ROLE_OPERATION(1000618, "不允许操作超级管理员角色"),
    ROLE_DATA_SCOPE_DENIED(1000619, "没有权限访问角色数据！"),
    ROLE_ASSIGNED_DELETE(1000620, "{0}已分配,不能删除"),
    POST_ASSIGNED_DELETE(1000621, "{0}已分配,不能删除"),
    DICT_TYPE_ASSIGNED_DELETE(1000622, "{0}已分配,不能删除"),
    ADMIN_LOGIN_PARAMETER_EMPTY(1000001, "用户名或密码不能为空"),
    ADMIN_LOGIN_CAPTCHA_INVALID(1000002, "验证码错误"),
    ADMIN_LOGIN_CAPTCHA_EXPIRED(1000003, "验证码已失效"),
    ADMIN_LOGIN_FAILED(1000004, "用户不存在/密码错误"),
    ADMIN_LOGIN_IP_BLOCKED(1000005, "很遗憾，访问IP已被列入系统黑名单"),
    ADMIN_LOGIN_AUTHENTICATION_ERROR(1000006, "登录认证异常：{0}"),
    ADMIN_LOGIN_RETRY_LIMIT_EXCEEDED(1000007, "密码输入错误{0}次，帐户锁定{1}分钟"),
    DOCTOR_REGISTER_USERNAME_EMPTY(2000001, "医生注册用户名不能为空"),
    DOCTOR_REGISTER_PASSWORD_EMPTY(2000002, "医生注册密码不能为空"),
    DOCTOR_REGISTER_USERNAME_LENGTH_INVALID(2000003, "医生注册用户名长度必须在2到20个字符之间"),
    DOCTOR_REGISTER_PASSWORD_LENGTH_INVALID(2000004, "医生注册密码长度必须在5到20个字符之间"),
    DOCTOR_REGISTER_USER_EXISTS(2000005, "注册失败，\"{0}\"已存在"),
    DOCTOR_REGISTER_FAILED(2000006, "医生注册失败，请联系系统管理人员"),
    DOCTOR_REGISTER_PHONE_EMPTY(2000008, "医生注册手机号不能为空"),
    DOCTOR_REGISTER_CODE_INVALID(2000011, "医生注册验证码错误"),
    DOCTOR_REGISTER_PHONE_EXISTS(2000033, "注册失败，手机号已被其他医生使用"),
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
    DOCTOR_USER_NOT_FOUND(2000024, "医生用户不存在"),
    DOCTOR_USER_REVIEW_STATUS_NOT_MATCH(2000025, "只允许审核待审核医生"),
    DOCTOR_USER_REVIEW_FAILED(2000026, "医生审核失败，请稍后重试"),
    DOCTOR_LOGIN_REVIEW_FAILED(2000027, "医生审核未通过，请使用原账号重新提交审核"),
    DOCTOR_CASE_DOCTOR_STATUS_NOT_OK(2000028, "医生资料审核通过后才可操作病例"),
    DOCTOR_PROFILE_SUBMIT_STATUS_NOT_MATCH(2000029, "当前医生状态不允许提交资料"),
    DOCTOR_PROFILE_SUBMIT_FAILED(2000030, "医生资料提交失败，请稍后重试"),
    DOCTOR_ACCOUNT_DELETE_STATUS_NOT_MATCH(2000031, "当前医生状态不允许删除账号"),
    DOCTOR_ACCOUNT_DELETE_FAILED(2000032, "医生账号删除失败，请稍后重试"),
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
