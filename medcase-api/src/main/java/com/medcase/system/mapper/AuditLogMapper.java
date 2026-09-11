package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.audit.AuditLogEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;

/**
 * @author suyh
 * @since 2024-09-02
 */
@Mapper
public interface AuditLogMapper extends BaseMapperX<AuditLogEntity> {
    default PageResult<AuditLogEntity> selectPage(
            PageParam pageParam, String userNicknameLike, String operationLike,
            String reqPathLike, Date beginTime, Date endTime) {
        LambdaQueryWrapper<AuditLogEntity> query = build()
                .likeIfPresent(AuditLogEntity::getUserNickname, userNicknameLike)
                .likeIfPresent(AuditLogEntity::getOperation, operationLike)
                .likeIfPresent(AuditLogEntity::getReqPath, reqPathLike)
                .geIfPresent(AuditLogEntity::getCreated, beginTime)
                .ltIfPresent(AuditLogEntity::getCreated, endTime)
                .orderByDesc(AuditLogEntity::getId);
        return selectPage(pageParam, query);
    }
}
