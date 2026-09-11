package com.medcase.web.controller.monitor;

import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.audit.AuditLogEntity;
import com.medcase.system.service.AuditLogService;
import com.medcase.web.controller.monitor.dto.AuditLogQueryRequest;
import com.medcase.web.controller.monitor.dto.AuditLogResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 审计日志记录。
 */
@RestController
@RequestMapping("/monitor/operlog")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public PageResult<AuditLogResponse> list(
            PageParam pageParam, AuditLogQueryRequest request) {
        PageResult<AuditLogEntity> entityPage = auditLogService.selectPage(pageParam, request);
        PageResult<AuditLogResponse> result = new PageResult<>();
        result.setList(entityPage.getList().stream().map(AuditLogResponse::new).toList());
        result.setTotal(entityPage.getTotal());
        return result;
    }
}
