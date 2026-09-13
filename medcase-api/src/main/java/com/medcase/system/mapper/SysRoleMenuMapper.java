package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.system.entity.SysRoleMenuEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Mapper
public interface SysRoleMenuMapper extends BaseMapperX<SysRoleMenuEntity> {
    default Long countByMenuId(Long menuId) {
        return selectCount(SysRoleMenuEntity::getMenuId, menuId);
    }

    default int deleteByRoleId(Long roleId) {
        LambdaQueryWrapperX<SysRoleMenuEntity> queryWrapper = build();
        queryWrapper.eq(SysRoleMenuEntity::getRoleId, roleId);
        return delete(queryWrapper);
    }

    default int deleteByRoleIds(Long[] roleIds) {
        LambdaQueryWrapperX<SysRoleMenuEntity> queryWrapper = build();
        queryWrapper.in(SysRoleMenuEntity::getRoleId, Arrays.asList(roleIds));
        return delete(queryWrapper);
    }

    default List<Long> selectMenuIdsByRoleId(Long roleId) {
        List<SysRoleMenuEntity> relations = selectList(SysRoleMenuEntity::getRoleId, roleId);
        if (relations == null || relations.isEmpty()) {
            return List.of();
        }
        return relations.stream().map(SysRoleMenuEntity::getMenuId).toList();
    }

    default void insertRoleMenus(Collection<SysRoleMenuEntity> entities) {
        insertBatch(entities);
    }
}
