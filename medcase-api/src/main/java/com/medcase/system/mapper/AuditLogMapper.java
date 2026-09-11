package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.mp.mybatis.BaseMapperX;
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
        LambdaQueryWrapper<AuditLogEntity> query = build()
                .likeIfPresent(AuditLogEntity::getUserNickname, request.getUserNicknameLike())
                .likeIfPresent(AuditLogEntity::getOperation, request.getOperationLike())
                .likeIfPresent(AuditLogEntity::getReqPath, request.getReqPathLike())
                .geIfPresent(AuditLogEntity::getCreated, request.getBeginTime())
                .ltIfPresent(AuditLogEntity::getCreated, request.getEndTime())
                .orderByDesc(AuditLogEntity::getId);
        return selectPage(pageParam, query);
    }
}
