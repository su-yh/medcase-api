package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.entity.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.Collection;

@Mapper
public interface SysUserRoleMapper extends BaseMapperX<SysUserRoleEntity> {
    default Long countByRoleId(Long roleId) {
        return selectCount(SysUserRoleEntity::getRoleId, roleId);
    }

    default int deleteByUserId(Long userId) {
        return delete(build().eq(SysUserRoleEntity::getUserId, userId));
    }

    default int deleteByUserIds(Long[] userIds) {
        return delete(build().in(SysUserRoleEntity::getUserId, Arrays.asList(userIds)));
    }

    default int deleteByRoleId(Long roleId) {
        return delete(build().eq(SysUserRoleEntity::getRoleId, roleId));
    }

    default int deleteByRoleIds(Long[] roleIds) {
        return delete(build().in(SysUserRoleEntity::getRoleId, Arrays.asList(roleIds)));
    }

    default int deleteByUserAndRole(Long userId, Long roleId) {
        return delete(build()
                .eq(SysUserRoleEntity::getUserId, userId)
                .eq(SysUserRoleEntity::getRoleId, roleId));
    }

    default int deleteByRoleAndUsers(Long roleId, Long[] userIds) {
        return delete(build()
                .eq(SysUserRoleEntity::getRoleId, roleId)
                .in(SysUserRoleEntity::getUserId, Arrays.asList(userIds)));
    }

    default void insertUserRoles(Collection<SysUserRoleEntity> entities) {
        insertBatch(entities);
    }
}
