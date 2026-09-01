package com.medcase.biz.request;

import com.medcase.common.constant.UserConstants;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.validation.groups.ValidationGroups;
import com.medcase.storage.pojo.FileAttachment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 医生端注册请求
 *
 * @author suyh
 * @since 2026-08-20
 */
@Data
public class DoctorRegisterRequest {
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

    @NotBlank(message = "姓名不能为空")
    @Size(max = 30, message = "姓名不能超过30个字符")
    private String nickName;

    @NotBlank(message = "性别不能为空")
    private String sex;

    @NotBlank(message = "身份证号码不能为空")
    @Size(max = 30, message = "身份证号码不能超过30个字符")
    private String idCardNumber;

    @NotBlank(message = "职称不能为空", groups = ValidationGroups.Doctor.Submit.class)
    @Size(max = 30, message = "职称不能超过30个字符", groups = ValidationGroups.Doctor.Submit.class)
    private String title;

    @NotBlank(message = "邀请人不能为空")
    private String inviteCode;

    @NotNull(message = "身份证正面图片不能为空")
    private FileAttachment idCardFront;

    @NotNull(message = "身份证反面图片不能为空")
    private FileAttachment idCardBack;

    @NotNull(message = "医师职业资格证图片不能为空", groups = ValidationGroups.Doctor.Submit.class)
    private FileAttachment qualificationCertificate;
}
