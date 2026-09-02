package com.medcase.biz.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 供应商保存请求。
 */
@Data
public class SupplierSaveRequest {
    private Long supplierId;

    @NotBlank(message = "供应商昵称不能为空")
    @Size(max = 30, message = "供应商昵称不能超过30个字符")
    private String nickName;

    @NotBlank(message = "供应商性别不能为空")
    private String sex;

    @NotBlank(message = "供应商手机号不能为空")
    @Size(max = 20, message = "供应商手机号不能超过20个字符")
    private String phone;

    @Email(message = "供应商邮箱格式不正确")
    @Size(max = 50, message = "供应商邮箱不能超过50个字符")
    private String email;

    @NotBlank(message = "供应商身份证号不能为空")
    @Size(max = 30, message = "供应商身份证号不能超过30个字符")
    private String idCardNumber;

    @NotBlank(message = "供应商状态不能为空")
    private String status;

    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;
}
