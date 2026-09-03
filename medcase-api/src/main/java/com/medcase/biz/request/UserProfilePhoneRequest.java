package com.medcase.biz.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 病例端手机号修改请求。
 */
@Data
public class UserProfilePhoneRequest {
    @NotBlank(message = "手机号不能为空")
    @Size(max = 20, message = "手机号不能超过20个字符")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;
}
