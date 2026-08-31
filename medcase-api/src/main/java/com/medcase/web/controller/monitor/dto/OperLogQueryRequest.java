package com.medcase.web.controller.monitor.dto;

import lombok.Data;

/**
 * 操作日志查询请求。
 */
@Data
public class OperLogQueryRequest {

    private String operIp;

    private String title;

    private Integer businessType;

    private Integer status;

    private String operName;

    private String beginTime;

    private String endTime;
}
