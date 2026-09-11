package com.medcase.system.service;

import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.audit.AuditLogEntity;
import com.medcase.system.mapper.AuditLogMapper;
import com.medcase.web.controller.monitor.dto.AuditLogQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 审计日志服务。
 */
@Service
public class AuditLogService {

    @Autowired
    private AuditLogMapper auditLogMapper;

    public PageResult<AuditLogEntity> selectPage(
            PageParam pageParam, AuditLogQueryRequest request) {
        return auditLogMapper.selectPage(
                pageParam, request.getUserNicknameLike(), request.getOperationLike(),
                request.getReqPathLike(), request.getBeginTime(), request.getEndTime());
    }
}
