package com.medcase.biz.request;

import lombok.Data;

/**
 * 病例端登录请求
 *
 * @author suyh
 * @since 2026-08-20
 */
@Data
public class UserLoginRequest {
    private String username;

    private String password;

    private String userType;

    private String code;

    private String uuid;
}
