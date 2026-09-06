package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
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
        return delete(build().eq(SysRoleMenuEntity::getRoleId, roleId));
    }

    default int deleteByRoleIds(Long[] roleIds) {
        return delete(build().in(SysRoleMenuEntity::getRoleId, Arrays.asList(roleIds)));
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
