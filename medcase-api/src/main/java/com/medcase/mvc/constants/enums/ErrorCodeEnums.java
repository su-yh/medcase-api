package com.medcase.mvc.constants.enums;

import com.medcase.mvc.error.IErrorCode;

/**
 * 错误码枚举
 *
 * @author suyh
 * @since 2025-05-16
 */
public enum ErrorCodeEnums implements IErrorCode {
    USER_NOT_LOGIN("error.code.user.not.login", "用户未登录"),
    ACCESS_DENIED("error.code.access.denied", "禁止访问"),
    NO_IMPLEMENT("error.code.no.implement", "功能代码还未实现"),
    SERVICE_ERROR("error.code.service.error", "服务错误"),
    SYSTEM_UNSUPPORTED("error.code.system.unsupported", "Not Supported: {0}"),
    RATE_LIMIT_EXCEEDED("error.code.rate.limit.exceeded", "访问过于频繁，请稍候再试"),
    ATTACHMENT_EMPTY("error.code.attachment.empty", "上传附件不能为空"),
    ATTACHMENT_UPLOAD_FAILED("error.code.attachment.upload.failed", "附件上传失败"),
    ATTACHMENT_DOWNLOAD_FAILED("error.code.attachment.download.failed", "附件下载失败"),
    ATTACHMENT_BUCKET_NOT_CONFIGURED("error.code.attachment.bucket.not.configured", "附件存储桶未配置"),
    ATTACHMENT_INVALID_PATH("error.code.attachment.invalid.path", "非法附件文件路径：{0}"),
    ATTACHMENT_CONFIG_MISSING("error.code.attachment.config.missing", "附件存储配置缺失：{0}"),
    USER_AVATAR_UPDATE_FAILED("error.code.user.avatar.update.failed", "用户头像更新失败"),
    USER_DELETED("error.code.user.deleted", "对不起，您的账号已被删除"),
    USER_BLOCKED("error.code.user.blocked", "用户已封禁，请联系管理员"),
    USER_ID_RESOLVE_FAILED("error.code.user.id.resolve.failed", "获取用户ID异常"),
    DEPT_ID_RESOLVE_FAILED("error.code.dept.id.resolve.failed", "获取部门ID异常"),
    USERNAME_RESOLVE_FAILED("error.code.username.resolve.failed", "获取用户账户异常"),
    LOGIN_USER_RESOLVE_FAILED("error.code.login.user.resolve.failed", "获取用户信息异常"),
    SUPER_ADMIN_USER_OPERATION("error.code.super.admin.user.operation", "不允许操作超级管理员用户"),
    USER_DATA_SCOPE_DENIED("error.code.user.data.scope.denied", "没有权限访问用户数据！"),
    CONFIG_BUILTIN_DELETE("error.code.config.builtin.delete", "内置参数【{0}】不能删除 "),
    MENU_SORT_SAVE_FAILED("error.code.menu.sort.save.failed", "保存排序异常，请联系管理员"),
    DEPT_DATA_SCOPE_DENIED("error.code.dept.data.scope.denied", "没有权限访问部门数据！"),
    DEPT_DISABLED("error.code.dept.disabled", "部门停用，不允许新增"),
    DEPT_SORT_SAVE_FAILED("error.code.dept.sort.save.failed", "保存排序异常，请联系管理员"),
    SUPER_ADMIN_ROLE_OPERATION("error.code.super.admin.role.operation", "不允许操作超级管理员角色"),
    ROLE_DATA_SCOPE_DENIED("error.code.role.data.scope.denied", "没有权限访问角色数据！"),
    ROLE_ASSIGNED_DELETE("error.code.role.assigned.delete", "{0}已分配,不能删除"),
    POST_ASSIGNED_DELETE("error.code.post.assigned.delete", "{0}已分配,不能删除"),
    DICT_TYPE_ASSIGNED_DELETE("error.code.dict.type.assigned.delete", "{0}已分配,不能删除"),
    ADMIN_LOGIN_PARAMETER_EMPTY("error.code.admin.login.parameter.empty", "用户名或密码不能为空"),
    ADMIN_LOGIN_CAPTCHA_INVALID("error.code.admin.login.captcha.invalid", "验证码错误"),
    ADMIN_LOGIN_CAPTCHA_EXPIRED("error.code.admin.login.captcha.expired", "验证码已失效"),
    ADMIN_LOGIN_FAILED("error.code.admin.login.failed", "用户不存在/密码错误"),
    ADMIN_LOGIN_IP_BLOCKED("error.code.admin.login.ip.blocked", "很遗憾，访问IP已被列入系统黑名单"),
    ADMIN_LOGIN_AUTHENTICATION_ERROR("error.code.admin.login.authentication.error", "登录认证异常：{0}"),
    ADMIN_LOGIN_RETRY_LIMIT_EXCEEDED("error.code.admin.login.retry.limit.exceeded", "密码输入错误{0}次，帐户锁定{1}分钟"),
    USER_REGISTER_USERNAME_EMPTY("error.code.doctor.register.username.empty", "医生注册用户名不能为空"),
    USER_REGISTER_PASSWORD_EMPTY("error.code.doctor.register.password.empty", "医生注册密码不能为空"),
    USER_REGISTER_USERNAME_LENGTH_INVALID("error.code.doctor.register.username.length.invalid", "医生注册用户名长度必须在2到20个字符之间"),
    USER_REGISTER_PASSWORD_LENGTH_INVALID("error.code.doctor.register.password.length.invalid", "医生注册密码长度必须在5到20个字符之间"),
    USER_REGISTER_USER_EXISTS("error.code.doctor.register.user.exists", "注册失败，\"{0}\"已存在"),
    USER_REGISTER_FAILED("error.code.doctor.register.failed", "医生注册失败，请联系系统管理人员"),
    USER_REGISTER_PHONE_EMPTY("error.code.doctor.register.phone.empty", "医生注册手机号不能为空"),
    USER_REGISTER_PHONE_INVALID("error.code.doctor.register.phone.invalid", "医生注册手机号格式不正确"),
    USER_REGISTER_PHONE_EXISTS("error.code.doctor.register.phone.exists", "注册失败，手机号已被其他医生使用"),
    USER_REGISTER_SMS_CODE_EMPTY("error.code.doctor.register.sms.code.empty", "短信验证码不能为空"),
    USER_REGISTER_SMS_CODE_INVALID("error.code.doctor.register.sms.code.invalid", "短信验证码错误"),
    USER_REGISTER_SMS_CODE_EXPIRED("error.code.doctor.register.sms.code.expired", "短信验证码已失效"),
    USER_REGISTER_SMS_CODE_SEND_TOO_FREQUENT(
            "error.code.doctor.register.sms.code.send.too.frequent", "验证码发送过于频繁，请稍后再试"),
    USER_REGISTER_SMS_CONFIG_MISSING(
            "error.code.doctor.register.sms.config.missing", "短信服务配置缺失，请联系系统管理员"),
    USER_REGISTER_SMS_SEND_FAILED(
            "error.code.doctor.register.sms.send.failed", "短信验证码发送失败，请稍后重试"),
    USER_REGISTER_NICKNAME_EMPTY("error.code.doctor.register.nickname.empty", "医生姓名不能为空"),
    USER_REGISTER_SEX_EMPTY("error.code.doctor.register.sex.empty", "性别不能为空"),
    USER_REGISTER_ID_CARD_NUMBER_EMPTY("error.code.doctor.register.id.card.number.empty", "身份证号码不能为空"),
    USER_REGISTER_TITLE_EMPTY("error.code.doctor.register.title.empty", "职称不能为空"),
    USER_REGISTER_INVITE_CODE_EMPTY("error.code.doctor.register.invite.code.empty", "邀请码不能为空"),
    USER_REGISTER_INVITE_CODE_INVALID("error.code.doctor.register.invite.code.invalid", "邀请码错误"),
    USER_REGISTER_ID_CARD_FRONT_EMPTY("error.code.doctor.register.id.card.front.empty", "身份证正面图片不能为空"),
    USER_REGISTER_ID_CARD_BACK_EMPTY("error.code.doctor.register.id.card.back.empty", "身份证反面图片不能为空"),
    USER_REGISTER_QUALIFICATION_CERTIFICATE_EMPTY(
            "error.code.doctor.register.qualification.certificate.empty", "医师职业资格证图片不能为空"),
    USER_LOGIN_USER_NOT_EXISTS("error.code.doctor.login.user.not.exists", "医生账号不存在"),
    CASE_NOT_FOUND("error.code.doctor.case.not.found", "病例不存在"),
    CASE_SUBMIT_FAILED("error.code.doctor.case.submit.failed", "病例提交失败，请稍后重试"),
    USER_LOGIN_FAILED("error.code.doctor.login.failed", "医生登录失败，请检查账号和密码"),
    CASE_UPDATE_REJECT("error.code.doctor.update.reject", "只允许修改自己的病例"),
    CASE_UPDATE_STATUS_NOT_MATCH("error.code.doctor.update.status.not.match", "只允许修改草稿病例"),
    USER_TYPE_NOT_MATCH("error.code.user.type.not.match", "禁止访问，用户类型错误"),
    CASE_DELETE_STATUS_NOT_MATCH("error.code.doctor.delete.status.not.match", "只允许删除草稿病例"),
    CASE_DELETE_FAILED("error.code.doctor.case.delete.failed", "病例删除失败，请稍后重试"),
    CASE_REVIEW_STATUS_NOT_MATCH("error.code.doctor.case.review.status.not.match", "只允许审核待审核病例"),
    CASE_REVIEW_REASON_EMPTY("error.code.doctor.case.review.reason.empty", "审核拒绝原因不能为空"),
    CASE_REVIEW_FAILED("error.code.doctor.case.review.failed", "病例审核失败，请稍后重试"),
    CASE_REVIEW_STATUS_INVALID("error.code.doctor.case.review.status.invalid", "审核状态无效"),
    CASE_SETTLE_STATUS_NOT_MATCH("error.code.doctor.case.settle.status.not.match", "只允许结算审核通过的待结算病例"),
    CASE_SETTLE_FAILED("error.code.doctor.case.settle.failed", "病例结算失败，请稍后重试"),
    USER_NOT_FOUND("error.code.doctor.user.not.found", "医生用户不存在"),
    USER_REVIEW_STATUS_NOT_MATCH("error.code.doctor.user.review.status.not.match", "只允许审核待审核医生"),
    USER_REVIEW_REASON_EMPTY("error.code.doctor.user.review.reason.empty", "审核拒绝原因不能为空"),
    USER_REVIEW_FAILED("error.code.doctor.user.review.failed", "医生审核失败，请稍后重试"),
    USER_LOGIN_REVIEW_FAILED("error.code.doctor.login.review.failed", "用户审核未通过，请使用原账号重新提交审核"),
    CASE_USER_STATUS_NOT_OK("error.code.doctor.case.doctor.status.not.ok", "用户资料审核通过后才可操作病例"),
    USER_PROFILE_SUBMIT_STATUS_NOT_MATCH("error.code.doctor.profile.submit.status.not.match", "当前用户状态不允许提交资料"),
    USER_PROFILE_SUBMIT_FAILED("error.code.doctor.profile.submit.failed", "用户资料提交失败，请稍后重试"),
    USER_ACCOUNT_DELETE_STATUS_NOT_MATCH("error.code.doctor.account.delete.status.not.match", "当前用户状态不允许删除账号"),
    USER_ACCOUNT_DELETE_FAILED("error.code.doctor.account.delete.failed", "当前用户状态不允许删除账号"),
    CAPTCHA_IMAGE_GENERATION_FAILED("error.code.captcha.image.generation.failed", "验证码生成失败，请稍后重试"),
    REPEAT_SUBMIT("error.code.repeat.submit", "{0}"),
    OPERATION_FAILED("error.code.operation.failed", "操作失败"),
    ROLE_NAME_EXISTS("error.code.role.name.exists", "角色名称已存在"),
    ROLE_KEY_EXISTS("error.code.role.key.exists", "角色权限已存在"),
    ROLE_UPDATE_FAILED("error.code.role.update.failed", "修改角色失败，请联系管理员"),
    ROLE_DATA_SCOPE_UPDATE_FAILED("error.code.role.data.scope.update.failed", "角色数据权限更新失败"),
    ROLE_STATUS_UPDATE_FAILED("error.code.role.status.update.failed", "角色状态更新失败"),
    ROLE_DELETE_FAILED("error.code.role.delete.failed", "角色删除失败"),
    ROLE_AUTH_USER_DELETE_FAILED("error.code.role.auth.user.delete.failed", "取消角色用户授权失败"),
    ROLE_AUTH_USER_SELECT_FAILED("error.code.role.auth.user.select.failed", "角色用户授权失败"),
    CONFIG_KEY_EXISTS("error.code.config.key.exists", "参数键名已存在"),
    CONFIG_OPERATION_FAILED("error.code.config.operation.failed", "参数操作失败"),
    MENU_NAME_EXISTS("error.code.menu.name.exists", "菜单名称已存在"),
    MENU_FRAME_URL_INVALID("error.code.menu.frame.url.invalid", "地址必须以http(s)://开头"),
    MENU_ROUTE_EXISTS("error.code.menu.route.exists", "路由名称或地址已存在"),
    MENU_PARENT_SELF("error.code.menu.parent.self", "上级菜单不能选择自己"),
    MENU_OPERATION_FAILED("error.code.menu.operation.failed", "菜单操作失败"),
    MENU_HAS_CHILDREN("error.code.menu.has.children", "存在子菜单，不允许删除"),
    MENU_ASSIGNED("error.code.menu.assigned", "菜单已分配，不允许删除"),
    DICT_TYPE_EXISTS("error.code.dict.type.exists", "字典类型已存在"),
    DICT_OPERATION_FAILED("error.code.dict.operation.failed", "字典操作失败"),
    POST_NAME_EXISTS("error.code.post.name.exists", "岗位名称已存在"),
    POST_CODE_EXISTS("error.code.post.code.exists", "岗位编码已存在"),
    POST_OPERATION_FAILED("error.code.post.operation.failed", "岗位操作失败"),
    DEPT_NAME_EXISTS("error.code.dept.name.exists", "部门名称已存在"),
    DEPT_PARENT_SELF("error.code.dept.parent.self", "上级部门不能是自己"),
    DEPT_ENABLED_CHILDREN("error.code.dept.enabled.children", "该部门包含未停用的子部门"),
    DEPT_HAS_CHILDREN("error.code.dept.has.children", "存在下级部门，不允许删除"),
    DEPT_HAS_USERS("error.code.dept.has.users", "部门存在用户，不允许删除"),
    DEPT_OPERATION_FAILED("error.code.dept.operation.failed", "部门操作失败"),
    NOTICE_OPERATION_FAILED("error.code.notice.operation.failed", "公告操作失败"),
    USERNAME_EXISTS("error.code.username.exists", "登录账号已存在"),
    PHONE_EXISTS("error.code.phone.exists", "手机号码已存在"),
    EMAIL_EXISTS("error.code.email.exists", "邮箱账号已存在"),
    USER_CANNOT_DELETE_SELF("error.code.user.cannot.delete.self", "当前用户不能删除"),
    USER_OPERATION_FAILED("error.code.user.operation.failed", "用户操作失败"),
    PROFILE_PHONE_EXISTS("error.code.profile.phone.exists", "手机号码已存在"),
    PROFILE_EMAIL_EXISTS("error.code.profile.email.exists", "邮箱账号已存在"),
    PROFILE_UPDATE_FAILED("error.code.profile.update.failed", "修改个人信息异常，请联系管理员"),
    PROFILE_OLD_PASSWORD_INVALID("error.code.profile.old.password.invalid", "旧密码错误"),
    PROFILE_PASSWORD_SAME("error.code.profile.password.same", "新密码不能与旧密码相同"),
    PROFILE_PASSWORD_UPDATE_FAILED("error.code.profile.password.update.failed", "修改密码异常，请联系管理员"),
    SCREEN_UNLOCK_PASSWORD_EMPTY("error.code.screen.unlock.password.empty", "密码不能为空"),
    SCREEN_UNLOCK_USER_NOT_FOUND("error.code.screen.unlock.user.not.found", "服务器超时，请重新登录"),
    SCREEN_UNLOCK_PASSWORD_INVALID("error.code.screen.unlock.password.invalid", "密码错误，请重新输入"),
    ADMIN_REGISTER_DISABLED("error.code.admin.register.disabled", "当前系统没有开启注册功能"),
    ADMIN_REGISTER_USERNAME_EMPTY("error.code.admin.register.username.empty", "用户名不能为空"),
    ADMIN_REGISTER_PASSWORD_EMPTY("error.code.admin.register.password.empty", "用户密码不能为空"),
    ADMIN_REGISTER_USERNAME_LENGTH_INVALID(
            "error.code.admin.register.username.length.invalid", "账户长度必须在2到20个字符之间"),
    ADMIN_REGISTER_PASSWORD_LENGTH_INVALID(
            "error.code.admin.register.password.length.invalid", "密码长度必须在5到50个字符之间"),
    ADMIN_REGISTER_USER_EXISTS("error.code.admin.register.user.exists", "保存用户'{0}'失败，注册账号已存在"),
    ADMIN_REGISTER_FAILED("error.code.admin.register.failed", "注册失败，请联系系统管理人员"),
    ;

    private final String code;
    private final String msg;

    ErrorCodeEnums(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }
}
