package com.ruoyi.biz.request;

import lombok.Data;

/**
 * 医生端注册请求
 *
 * @author suyh
 * @since 2026-08-20
 */
@Data
public class DoctorRegisterRequest {
    private String username;

    private String password;

    private String phone;

    private String code;
}
