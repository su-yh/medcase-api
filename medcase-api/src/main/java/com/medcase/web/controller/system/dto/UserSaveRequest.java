package com.medcase.web.controller.system.dto;

import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.xss.Xss;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户保存请求。
 */
@Data
public class UserSaveRequest {

    private Long userId;

    private Long deptId;

    @Xss(message = "用户账号不能包含脚本字符")
    @NotBlank(message = "用户账号不能为空")
    @Size(max = 30, message = "用户账号长度不能超过30个字符")
    private String userName;

    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(max = 30, message = "用户昵称长度不能超过30个字符")
    private String nickName;

    private UserTypeEnums userType;

    @Email(message = "邮箱格式不正确")
    @Size(max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    @Size(max = 11, message = "手机号码长度不能超过11个字符")
    private String phonenumber;

    private String sex;

    private String password;

    private String status;

    private String remark;

    private Long[] roleIds;

    private Long[] postIds;
}
