package com.medcase.system.plus.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.plus.entity.SysRoleDeptEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Mapper
public interface SysRoleDeptMapper extends BaseMapperX<SysRoleDeptEntity> {
    default int deleteByRoleId(Long roleId) {
        return delete(build().eq(SysRoleDeptEntity::getRoleId, roleId));
    }

    default int deleteByRoleIds(Long[] roleIds) {
        return delete(build().in(SysRoleDeptEntity::getRoleId, Arrays.asList(roleIds)));
    }

    default void insertRoleDepts(Collection<SysRoleDeptEntity> entities) {
        insertBatch(entities);
    }
}
