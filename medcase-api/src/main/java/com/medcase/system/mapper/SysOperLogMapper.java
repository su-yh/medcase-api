package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.system.entity.SysOperLogEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysOperLogMapper extends BaseMapperX<SysOperLogEntity> {
    default List<SysOperLogEntity> selectOperLogList(
            String operIp, String title, Integer businessType,
            Integer[] businessTypes, Integer status, String operName,
            Object beginTime, Object endTime) {
        LambdaQueryWrapper<SysOperLogEntity> query = build()
                .likeIfPresent(SysOperLogEntity::getOperIp, operIp)
                .likeIfPresent(SysOperLogEntity::getTitle, title)
                .eqIfPresent(SysOperLogEntity::getBusinessType, businessType)
                .inIfPresent(SysOperLogEntity::getBusinessType, java.util.Arrays.asList(businessTypes))
                .eqIfPresent(SysOperLogEntity::getStatus, status)
                .likeIfPresent(SysOperLogEntity::getOperName, operName)
                .geIfPresent(SysOperLogEntity::getOperTime, beginTime)
                .leIfPresent(SysOperLogEntity::getOperTime, endTime)
                .orderByDesc(SysOperLogEntity::getOperId);
        return selectList(query);
    }

    default int cleanOperLog() {
        return delete(build());
    }
}
