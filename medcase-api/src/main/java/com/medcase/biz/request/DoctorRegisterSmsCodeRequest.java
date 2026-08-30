package com.medcase.biz.request;

import lombok.Data;

/**
 * 医生注册短信验证码请求。
 *
 * @author suyh
 */
@Data
public class DoctorRegisterSmsCodeRequest {
    private String phone;
}
