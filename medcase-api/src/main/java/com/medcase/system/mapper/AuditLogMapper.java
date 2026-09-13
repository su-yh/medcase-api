package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.audit.AuditLogEntity;
import com.medcase.web.controller.monitor.dto.AuditLogQueryRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author suyh
 * @since 2024-09-02
 */
@Mapper
public interface AuditLogMapper extends BaseMapperX<AuditLogEntity> {
    default PageResult<AuditLogEntity> selectPage(
            PageParam pageParam, AuditLogQueryRequest request) {
        LambdaQueryWrapperX<AuditLogEntity> query = build();
        query.likeIfPresent(AuditLogEntity::getUserNickname, request.getUserNicknameLike());
        query.likeIfPresent(AuditLogEntity::getOperation, request.getOperationLike());
        query.likeIfPresent(AuditLogEntity::getReqPath, request.getReqPathLike());
        query.geIfPresent(AuditLogEntity::getCreated, request.getBeginTime());
        query.ltIfPresent(AuditLogEntity::getCreated, request.getEndTime());
        query.orderByDesc(AuditLogEntity::getId);
        return selectPage(pageParam, query);
    }
}
