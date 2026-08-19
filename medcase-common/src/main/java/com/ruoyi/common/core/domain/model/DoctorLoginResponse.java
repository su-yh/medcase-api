package com.ruoyi.common.core.domain.model;

/**
 * 医生端登录返回对象
 *
 * @author suyh
 */
public class DoctorLoginResponse {
    private String token;

    public DoctorLoginResponse() {
    }

    public DoctorLoginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
