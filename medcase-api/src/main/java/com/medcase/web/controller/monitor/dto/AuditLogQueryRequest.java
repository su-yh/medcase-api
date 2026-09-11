package com.medcase.web.controller.monitor.dto;

import com.medcase.mvc.advice.date.DateTimeFormatPlus;
import com.medcase.mvc.advice.date.OffsetUnit;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 审计日志查询请求。
 */
@Data
public class AuditLogQueryRequest {

    private String userNicknameLike;

    private String operationLike;

    private String reqPathLike;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    @DateTimeFormatPlus(pattern = "yyyy-MM-dd", offset = 1, unit = OffsetUnit.DAY)
    private Date endTime;
}
