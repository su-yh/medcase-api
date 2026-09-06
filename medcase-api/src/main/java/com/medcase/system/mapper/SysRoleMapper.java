package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.medcase.mp.mybatis.BaseMapperX;
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
        return selectPage(pageParam, build()
                .likeIfPresent(SysRoleEntity::getRoleName, request.getRoleNameLike())
                .likeIfPresent(SysRoleEntity::getRoleKey, request.getRoleKeyLike())
                .eqIfPresent(SysRoleEntity::getStatus, request.getStatus())
                .geIfPresent(SysRoleEntity::getCreateTime, request.getBeginTime())
                .ltIfPresent(SysRoleEntity::getCreateTime, request.getEndTime())
                .eqIfPresent(SysRoleEntity::getCreateUserId, createUserId)
                .eq(SysRoleEntity::getDelFlag, "0")
                .orderByAsc(SysRoleEntity::getRoleSort));
    }

    default SysRoleEntity selectRoleByName(String roleName) {
        return selectOne(build()
                .eq(SysRoleEntity::getRoleName, roleName)
                .eq(SysRoleEntity::getDelFlag, "0"));
    }

    default SysRoleEntity selectRoleByKey(String roleKey) {
        return selectOne(build()
                .eq(SysRoleEntity::getRoleKey, roleKey)
                .eq(SysRoleEntity::getDelFlag, "0"));
    }

    default int deleteRolesByIds(Long[] roleIds) {
        SysRoleEntity entity = new SysRoleEntity();
        entity.setDelFlag("2");
        return update(entity, new LambdaUpdateWrapper<SysRoleEntity>()
                .in(SysRoleEntity::getRoleId, Arrays.asList(roleIds)));
    }
}
