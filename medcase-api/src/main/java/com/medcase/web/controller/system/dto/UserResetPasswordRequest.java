package com.medcase.web.controller.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户密码重置请求。
 */
@Data
public class UserResetPasswordRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "用户密码不能为空")
    private String password;
}
