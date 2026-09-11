package com.medcase.web.controller.monitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medcase.mvc.audit.AuditLogEntity;
import lombok.Getter;

import java.util.Date;

/**
 * 审计日志响应。
 */
@Getter
public class AuditLogResponse {

    private final Long id;

    private final String traceId;

    private final Long userId;

    private final String userNickname;

    private final String operation;

    private final String reqMethod;

    private final String reqPath;

    private final String reqArgument;

    private final String resultDetail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final Date created;

    public AuditLogResponse(AuditLogEntity entity) {
        this.id = entity.getId();
        this.traceId = entity.getTraceId();
        this.userId = entity.getUserId();
        this.userNickname = entity.getUserNickname();
        this.operation = entity.getOperation() == null ? null : entity.getOperation().getDesc();
        this.reqMethod = entity.getReqMethod();
        this.reqPath = entity.getReqPath();
        this.reqArgument = entity.getReqArgument();
        this.resultDetail = entity.getResultDetail();
        this.created = entity.getCreated();
    }
}
