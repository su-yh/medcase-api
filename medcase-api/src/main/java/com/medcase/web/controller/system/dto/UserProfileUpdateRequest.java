package com.medcase.web.controller.system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户个人资料修改请求。
 */
@Data
public class UserProfileUpdateRequest {

    private String nickName;

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    private String phonenumber;

    private String sex;
}
