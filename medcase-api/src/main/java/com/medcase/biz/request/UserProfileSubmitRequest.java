package com.medcase.biz.request;

import com.medcase.common.validation.groups.ValidationGroups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

/**
 * 病例端资料提交请求
 *
 * @author suyh
 */
@Data
public class UserProfileSubmitRequest {
    @NotBlank(message = "姓名不能为空")
    @Size(max = 30, message = "姓名不能超过30个字符")
    private String nickName;

    @NotBlank(message = "性别不能为空")
    private String sex;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 20, message = "手机号不能超过20个字符")
    private String phone;

    @NotBlank(message = "身份证号码不能为空")
    @Size(max = 30, message = "身份证号码不能超过30个字符")
    private String idCardNumber;

    @NotBlank(message = "职称不能为空", groups = ValidationGroups.Doctor.Submit.class)
    @Size(max = 30, message = "职称不能超过30个字符", groups = ValidationGroups.Doctor.Submit.class)
    private String title;

    @NotNull(message = "身份证正面图片不能为空")
    private FileAttachment idCardFront;

    @NotNull(message = "身份证反面图片不能为空")
    private FileAttachment idCardBack;

    @NotNull(message = "医师职业资格证图片不能为空", groups = ValidationGroups.Doctor.Submit.class)
    private FileAttachment qualificationCertificate;
}
