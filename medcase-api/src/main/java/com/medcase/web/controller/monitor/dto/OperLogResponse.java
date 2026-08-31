package com.medcase.web.controller.monitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medcase.system.entity.SysOperLogEntity;
import lombok.Getter;

import java.util.Date;

/**
 * 操作日志响应。
 */
@Getter
public class OperLogResponse {

    private final Long operId;

    private final String title;

    private final Integer businessType;

    private final String method;

    private final String requestMethod;

    private final Integer operatorType;

    private final String operName;

    private final String deptName;

    private final String operUrl;

    private final String operIp;

    private final String operLocation;

    private final String operParam;

    private final String jsonResult;

    private final Integer status;

    private final String errorMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final Date operTime;

    private final Long costTime;

    public OperLogResponse(SysOperLogEntity entity) {
        this.operId = entity.getOperId();
        this.title = entity.getTitle();
        this.businessType = entity.getBusinessType();
        this.method = entity.getMethod();
        this.requestMethod = entity.getRequestMethod();
        this.operatorType = entity.getOperatorType();
        this.operName = entity.getOperName();
        this.deptName = entity.getDeptName();
        this.operUrl = entity.getOperUrl();
        this.operIp = entity.getOperIp();
        this.operLocation = entity.getOperLocation();
        this.operParam = entity.getOperParam();
        this.jsonResult = entity.getJsonResult();
        this.status = entity.getStatus();
        this.errorMsg = entity.getErrorMsg();
        this.operTime = entity.getOperTime();
        this.costTime = entity.getCostTime();
    }
}
