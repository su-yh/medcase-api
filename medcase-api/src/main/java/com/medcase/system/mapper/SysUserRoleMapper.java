package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.system.entity.SysUserRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapperX<SysUserRoleEntity> {
    default Long countByRoleId(Long roleId) {
        return selectCount(SysUserRoleEntity::getRoleId, roleId);
    }

    default int deleteByUserId(Long userId) {
        LambdaQueryWrapperX<SysUserRoleEntity> queryWrapper = build();
        queryWrapper.eq(SysUserRoleEntity::getUserId, userId);
        return delete(queryWrapper);
    }

    default int deleteByUserIds(Long[] userIds) {
        LambdaQueryWrapperX<SysUserRoleEntity> queryWrapper = build();
        queryWrapper.in(SysUserRoleEntity::getUserId, Arrays.asList(userIds));
        return delete(queryWrapper);
    }

    default int deleteByUserAndRole(Long userId, Long roleId) {
        LambdaQueryWrapperX<SysUserRoleEntity> queryWrapper = build();
        queryWrapper.eq(SysUserRoleEntity::getUserId, userId);
        queryWrapper.eq(SysUserRoleEntity::getRoleId, roleId);
        return delete(queryWrapper);
    }

    default int deleteByRoleAndUsers(Long roleId, Long[] userIds) {
        LambdaQueryWrapperX<SysUserRoleEntity> queryWrapper = build();
        queryWrapper.eq(SysUserRoleEntity::getRoleId, roleId);
        queryWrapper.in(SysUserRoleEntity::getUserId, Arrays.asList(userIds));
        return delete(queryWrapper);
    }

    default void insertUserRoles(Collection<SysUserRoleEntity> entities) {
        insertBatch(entities);
    }

    default List<SysUserRoleEntity> selectByUserId(Long userId) {
        if (userId == null) {
            return null;
        }

        LambdaQueryWrapperX<SysUserRoleEntity> queryWrapperX = build();
        queryWrapperX.eq(SysUserRoleEntity::getUserId, userId);

        return selectList(queryWrapperX);
    }
}
