package com.medcase.system.converter;

import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.common.core.domain.entity.SysDictType;
import com.medcase.common.core.domain.entity.SysMenu;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.system.entity.SysDeptEntity;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.entity.SysDictTypeEntity;
import com.medcase.system.entity.SysMenuEntity;
import com.medcase.system.entity.SysNoticeEntity;
import com.medcase.system.entity.SysPostEntity;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.entity.SysUserEntity;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * 系统旧领域对象与 MyBatis-Plus 持久化实体之间的转换。
 */
public final class SystemEntityConverter {

    private SystemEntityConverter() {
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

    public static SysMenuEntity toEntity(SysMenu source) {
        return copy(source, SysMenuEntity.class);
    }

    public static SysMenu toDomain(SysMenuEntity source) {
        return copy(source, SysMenu.class);
    }

    public static SysRoleEntity toEntity(SysRole source) {
        return copy(source, SysRoleEntity.class);
    }

    public static SysRole toDomain(SysRoleEntity source) {
        return copy(source, SysRole.class);
    }

    public static SysUserEntity toEntity(SysUser source) {
        return copy(source, SysUserEntity.class);
    }

    public static SysUser toDomain(SysUserEntity source) {
        return copy(source, SysUser.class);
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
