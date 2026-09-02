package com.medcase.biz.request;

import com.medcase.common.constant.UserConstants;
import com.medcase.common.enums.UserTypeEnums;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 病例端注册请求
 *
 * @author suyh
 * @since 2026-08-20
 */
@Data
public class UserRegisterRequest {
    @NotBlank(message = "注册账号不能为空")
    @Size(min = UserConstants.USERNAME_MIN_LENGTH, max = UserConstants.USERNAME_MAX_LENGTH,
            message = "注册账号长度必须在2到20个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH,
            message = "密码长度必须在5到20个字符之间")
    private String password;

    @NotNull(message = "用户类型不能为空")
    private UserTypeEnums userType;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 20, message = "手机号不能超过20个字符")
    private String phone;

    @NotBlank(message = "短信验证码不能为空")
    private String smsCode;
}
