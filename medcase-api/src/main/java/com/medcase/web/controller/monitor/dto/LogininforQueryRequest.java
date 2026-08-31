package com.medcase.web.controller.monitor.dto;

import lombok.Data;

/**
 * 登录日志查询请求。
 */
@Data
public class LogininforQueryRequest {

    private String ipaddr;

    private String userName;

    private String status;

    private String beginTime;

    private String endTime;
}
