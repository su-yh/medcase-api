package com.medcase.system.plus.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.system.plus.entity.SysLogininforEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface SysLogininforMapper extends BaseMapperX<SysLogininforEntity> {
    default List<SysLogininforEntity> selectLogininforList(
            String ipaddr, String status, String userName,
            Object beginTime, Object endTime) {
        LambdaQueryWrapper<SysLogininforEntity> query = build()
                .likeIfPresent(SysLogininforEntity::getIpaddr, ipaddr)
                .eqIfPresent(SysLogininforEntity::getStatus, status)
                .likeIfPresent(SysLogininforEntity::getUserName, userName)
                .geIfPresent(SysLogininforEntity::getLoginTime, beginTime)
                .leIfPresent(SysLogininforEntity::getLoginTime, endTime)
                .orderByDesc(SysLogininforEntity::getInfoId);
        return selectList(query);
    }

    default int cleanLogininfor() {
        return delete(build());
    }
}
