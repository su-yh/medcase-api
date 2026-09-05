package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.web.controller.system.dto.RoleQueryRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapperX<SysRoleEntity> {
    default PageResult<SysRoleEntity> selectPage(PageParam pageParam, RoleQueryRequest request) {
        return selectPage(pageParam, build()
                .likeIfPresent(SysRoleEntity::getRoleName, request.getRoleNameLike())
                .likeIfPresent(SysRoleEntity::getRoleKey, request.getRoleKeyLike())
                .eqIfPresent(SysRoleEntity::getStatus, request.getStatus())
                .geIfPresent(SysRoleEntity::getCreateTime, request.getBeginTime())
                .ltIfPresent(SysRoleEntity::getCreateTime, request.getEndTime())
                .eq(SysRoleEntity::getDelFlag, "0")
                .orderByAsc(SysRoleEntity::getRoleSort));
    }

    List<SysRole> selectRolePermissionByUserId(Long userId);

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
        return update(null, new LambdaUpdateWrapper<SysRoleEntity>()
                .set(SysRoleEntity::getDelFlag, "2")
                .in(SysRoleEntity::getRoleId, Arrays.asList(roleIds)));
    }
}
