package com.medcase.web.controller.monitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medcase.system.entity.SysLogininforEntity;
import lombok.Getter;

import java.util.Date;

/**
 * 登录日志响应。
 */
@Getter
public class LogininforResponse {

    private final Long infoId;

    private final String userName;

    private final String status;

    private final String ipaddr;

    private final String loginLocation;

    private final String browser;

    private final String os;

    private final String msg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final Date loginTime;

    public LogininforResponse(SysLogininforEntity entity) {
        this.infoId = entity.getInfoId();
        this.userName = entity.getUserName();
        this.status = entity.getStatus();
        this.ipaddr = entity.getIpaddr();
        this.loginLocation = entity.getLoginLocation();
        this.browser = entity.getBrowser();
        this.os = entity.getOs();
        this.msg = entity.getMsg();
        this.loginTime = entity.getLoginTime();
    }
}
