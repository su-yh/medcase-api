package com.medcase.system.plus;

import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.common.core.domain.entity.SysDictType;
import com.medcase.common.core.domain.entity.SysMenu;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.system.domain.SysConfig;
import com.medcase.system.domain.SysLogininfor;
import com.medcase.system.domain.SysNotice;
import com.medcase.system.domain.SysNoticeRead;
import com.medcase.system.domain.SysOperLog;
import com.medcase.system.domain.SysPost;
import com.medcase.system.domain.SysRoleDept;
import com.medcase.system.domain.SysRoleMenu;
import com.medcase.system.domain.SysUserPost;
import com.medcase.system.domain.SysUserRole;
import com.medcase.system.plus.entity.SysConfigEntity;
import com.medcase.system.plus.entity.SysDeptEntity;
import com.medcase.system.plus.entity.SysDictDataEntity;
import com.medcase.system.plus.entity.SysDictTypeEntity;
import com.medcase.system.plus.entity.SysLogininforEntity;
import com.medcase.system.plus.entity.SysMenuEntity;
import com.medcase.system.plus.entity.SysNoticeEntity;
import com.medcase.system.plus.entity.SysNoticeReadEntity;
import com.medcase.system.plus.entity.SysOperLogEntity;
import com.medcase.system.plus.entity.SysPostEntity;
import com.medcase.system.plus.entity.SysRoleDeptEntity;
import com.medcase.system.plus.entity.SysRoleEntity;
import com.medcase.system.plus.entity.SysRoleMenuEntity;
import com.medcase.system.plus.entity.SysUserEntity;
import com.medcase.system.plus.entity.SysUserPostEntity;
import com.medcase.system.plus.entity.SysUserRoleEntity;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 系统旧领域对象与 MyBatis-Plus 持久化实体之间的转换。
 */
public final class SystemEntityConverter {

    private SystemEntityConverter() {
    }

    public static SysConfigEntity toEntity(SysConfig source) {
        return copy(source, SysConfigEntity.class);
    }

    public static SysConfig toDomain(SysConfigEntity source) {
        return copy(source, SysConfig.class);
    }

    public static SysDeptEntity toEntity(SysDept source) {
        return copy(source, SysDeptEntity.class);
    }

    public static SysDept toDomain(SysDeptEntity source) {
        return copy(source, SysDept.class);
    }

    public static SysDictDataEntity toEntity(SysDictData source) {
        return copy(source, SysDictDataEntity.class);
    }

    public static SysDictData toDomain(SysDictDataEntity source) {
        return copy(source, SysDictData.class);
    }

    public static SysDictTypeEntity toEntity(SysDictType source) {
        return copy(source, SysDictTypeEntity.class);
    }

    public static SysDictType toDomain(SysDictTypeEntity source) {
        return copy(source, SysDictType.class);
    }

    public static SysLogininforEntity toEntity(SysLogininfor source) {
        return copy(source, SysLogininforEntity.class);
    }

    public static SysLogininfor toDomain(SysLogininforEntity source) {
        return copy(source, SysLogininfor.class);
    }

    public static SysMenuEntity toEntity(SysMenu source) {
        return copy(source, SysMenuEntity.class);
    }

    public static SysMenu toDomain(SysMenuEntity source) {
        return copy(source, SysMenu.class);
    }

    public static SysNoticeEntity toEntity(SysNotice source) {
        return copy(source, SysNoticeEntity.class);
    }

    public static SysNotice toDomain(SysNoticeEntity source) {
        return copy(source, SysNotice.class);
    }

    public static SysNoticeReadEntity toEntity(SysNoticeRead source) {
        return copy(source, SysNoticeReadEntity.class);
    }

    public static SysNoticeRead toDomain(SysNoticeReadEntity source) {
        return copy(source, SysNoticeRead.class);
    }

    public static SysOperLogEntity toEntity(SysOperLog source) {
        return copy(source, SysOperLogEntity.class);
    }

    public static SysOperLog toDomain(SysOperLogEntity source) {
        return copy(source, SysOperLog.class);
    }

    public static SysPostEntity toEntity(SysPost source) {
        return copy(source, SysPostEntity.class);
    }

    public static SysPost toDomain(SysPostEntity source) {
        return copy(source, SysPost.class);
    }

    public static SysRoleDeptEntity toEntity(SysRoleDept source) {
        return copy(source, SysRoleDeptEntity.class);
    }

    public static SysRoleDept toDomain(SysRoleDeptEntity source) {
        return copy(source, SysRoleDept.class);
    }

    public static SysRoleEntity toEntity(SysRole source) {
        return copy(source, SysRoleEntity.class);
    }

    public static SysRole toDomain(SysRoleEntity source) {
        return copy(source, SysRole.class);
    }

    public static SysRoleMenuEntity toEntity(SysRoleMenu source) {
        return copy(source, SysRoleMenuEntity.class);
    }

    public static SysRoleMenu toDomain(SysRoleMenuEntity source) {
        return copy(source, SysRoleMenu.class);
    }

    public static SysUserEntity toEntity(SysUser source) {
        return copy(source, SysUserEntity.class);
    }

    public static SysUser toDomain(SysUserEntity source) {
        return copy(source, SysUser.class);
    }

    public static SysUserPostEntity toEntity(SysUserPost source) {
        return copy(source, SysUserPostEntity.class);
    }

    public static SysUserPost toDomain(SysUserPostEntity source) {
        return copy(source, SysUserPost.class);
    }

    public static SysUserRoleEntity toEntity(SysUserRole source) {
        return copy(source, SysUserRoleEntity.class);
    }

    public static SysUserRole toDomain(SysUserRoleEntity source) {
        return copy(source, SysUserRole.class);
    }

    public static <S, T> List<T> copyList(List<S> sources, Class<T> targetType) {
        return sources.stream().map(source -> copy(source, targetType)).toList();
    }

    private static <S, T> T copy(S source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        try {
            T target = targetType.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        }
        catch (ReflectiveOperationException e) {
            throw new IllegalStateException("无法创建实体对象: " + targetType.getName(), e);
        }
    }
}
