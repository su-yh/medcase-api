package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.web.controller.system.dto.RoleQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;

@Mapper
public interface SysRoleMapper extends BaseMapperX<SysRoleEntity> {
    default PageResult<SysRoleEntity> selectPage(
            PageParam pageParam, RoleQueryRequest request, Long createUserId) {
        LambdaQueryWrapperX<SysRoleEntity> query = build();
        query.likeIfPresent(SysRoleEntity::getRoleName, request.getRoleNameLike());
        query.likeIfPresent(SysRoleEntity::getRoleKey, request.getRoleKeyLike());
        query.eqIfPresent(SysRoleEntity::getStatus, request.getStatus());
        query.geIfPresent(SysRoleEntity::getCreateTime, request.getBeginTime());
        query.ltIfPresent(SysRoleEntity::getCreateTime, request.getEndTime());
        query.eqIfPresent(SysRoleEntity::getCreateUserId, createUserId);
        query.eq(SysRoleEntity::getDelFlag, "0");
        query.orderByAsc(SysRoleEntity::getRoleSort);
        return selectPage(pageParam, query);
    }

    default SysRoleEntity selectRoleByName(String roleName) {
        LambdaQueryWrapperX<SysRoleEntity> query = build();
        query.eq(SysRoleEntity::getRoleName, roleName);
        query.eq(SysRoleEntity::getDelFlag, "0");
        return selectOne(query);
    }

    default SysRoleEntity selectRoleByKey(String roleKey) {
        LambdaQueryWrapperX<SysRoleEntity> query = build();
        query.eq(SysRoleEntity::getRoleKey, roleKey);
        query.eq(SysRoleEntity::getDelFlag, "0");
        return selectOne(query);
    }

    default int deleteRolesByIds(Long[] roleIds) {
        SysRoleEntity entity = new SysRoleEntity();
        entity.setDelFlag("2");
        return update(entity, new LambdaUpdateWrapper<SysRoleEntity>()
                .in(SysRoleEntity::getRoleId, Arrays.asList(roleIds)));
    }
}
