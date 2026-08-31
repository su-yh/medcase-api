package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.system.entity.SysRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapperX<SysRoleEntity> {
    List<SysRole> selectRoleList(SysRole role);

    List<SysRole> selectRolePermissionByUserId(Long userId);

    List<SysRole> selectRoleAll();

    List<Long> selectRoleListByUserId(Long userId);

    List<SysRole> selectRolesByUserName(String userName);

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

    default int deleteRoleById(Long roleId) {
        return update(null, new LambdaUpdateWrapper<SysRoleEntity>()
                .set(SysRoleEntity::getDelFlag, "2")
                .eq(SysRoleEntity::getRoleId, roleId));
    }

    default int deleteRolesByIds(Long[] roleIds) {
        return update(null, new LambdaUpdateWrapper<SysRoleEntity>()
                .set(SysRoleEntity::getDelFlag, "2")
                .in(SysRoleEntity::getRoleId, Arrays.asList(roleIds)));
    }
}
