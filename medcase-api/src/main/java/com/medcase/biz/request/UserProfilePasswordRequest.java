package com.medcase.biz.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 病例端密码修改请求。
 */
@Data
public class UserProfilePasswordRequest {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    @Size(min = 5, max = 20, message = "密码长度必须在5到20个字符之间")
    private String newPassword;
}
