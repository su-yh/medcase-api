package com.medcase.system.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.entity.SysRoleMenuEntity;
import com.medcase.system.entity.SysUserRoleEntity;
import com.medcase.system.mapper.SysRoleMapper;
import com.medcase.system.mapper.SysRoleMenuMapper;
import com.medcase.system.mapper.SysUserRoleMapper;
import com.medcase.web.controller.system.dto.RoleQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 角色 业务层处理
 * 
 */
@Service
public class SysRoleService {

    private static final String ALL_ROLES_CACHE_KEY = "allRoles";

    private static final long ROLE_CACHE_EXPIRE_MINUTES = 30L;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    private final Cache<String, List<SysRoleEntity>> roleCache = Caffeine.newBuilder()
            .expireAfterWrite(ROLE_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .build();

    private final Object roleCacheLoadLock = new Object();

    /**
     * 根据条件分页查询角色数据
     * @return 角色数据集合信息
     */
    public PageResult<SysRoleEntity> selectPage(PageParam pageParam, RoleQueryRequest request) {
        return roleMapper.selectPage(pageParam, request);
    }

    /**
     * 根据用户ID查询角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<SysRoleEntity> selectRolesByUserId(Long userId) {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<SysRoleEntity> roles = SystemEntityConverter.copyList(selectRoleAll(), SysRoleEntity.class);
        for (SysRoleEntity role : roles) {
            for (SysRole userRole : userRoles) {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    public Set<String> selectRolePermissionByUserId(Long userId) {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms) {
            if (perm != null) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     * 
     * @return 角色列表
     */
    public List<SysRoleEntity> selectRoleAll() {
        List<SysRoleEntity> roles = roleCache.getIfPresent(ALL_ROLES_CACHE_KEY);
        if (roles != null) {
            return roles;
        }

        synchronized (roleCacheLoadLock) {
            roles = roleCache.getIfPresent(ALL_ROLES_CACHE_KEY);
            if (roles != null) {
                return roles;
            }

            roles = roleMapper.selectList();
            if (roles == null) {
                roles = List.of();
            }
            roleCache.put(ALL_ROLES_CACHE_KEY, roles);
        }
        return roles;
    }

    /**
     * 通过角色ID查询角色
     * 
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public SysRoleEntity selectRoleById(Long roleId) {
        if (roleId == null) {
            return null;
        }
        return selectRoleAll().stream()
                .filter(role -> roleId.equals(role.getRoleId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 校验角色名称是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    public boolean checkRoleNameUnique(SysRole role) {
        long roleId = role.getRoleId() == null ? -1L : role.getRoleId();
        SysRoleEntity sysRoleEntity = roleMapper.selectRoleByName(role.getRoleName());
        if (sysRoleEntity != null && sysRoleEntity.getRoleId() != roleId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    public boolean checkRoleKeyUnique(SysRole role) {
        long roleId = role.getRoleId() == null ? -1L : role.getRoleId();
        SysRoleEntity info = roleMapper.selectRoleByKey(role.getRoleKey());
        if (info != null && info.getRoleId() != roleId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 通过角色ID查询角色使用数量
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    public int countUserRoleByRoleId(Long roleId) {
        return Math.toIntExact(userRoleMapper.countByRoleId(roleId));
    }

    /**
     * 新增保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Transactional
    public int insertRole(SysRole role) {
        SysRoleEntity entity = SystemEntityConverter.toEntity(role);
        int row = roleMapper.insert(entity);
        role.setRoleId(entity.getRoleId());
        if (row > 0) {
            synchronized (roleCacheLoadLock) {
                roleCache.invalidate(ALL_ROLES_CACHE_KEY);
            }
        }
        return insertRoleMenu(role);
    }

    /**
     * 修改保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Transactional
    public int updateRole(SysRole role) {
        roleMapper.updateById(SystemEntityConverter.toEntity(role));
        // 删除角色与菜单关联
        roleMenuMapper.deleteByRoleId(role.getRoleId());
        synchronized (roleCacheLoadLock) {
            roleCache.invalidate(ALL_ROLES_CACHE_KEY);
        }
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int updateRoleStatus(SysRole role) {
        int row = roleMapper.updateById(SystemEntityConverter.toEntity(role));
        if (row > 0) {
            synchronized (roleCacheLoadLock) {
                roleCache.invalidate(ALL_ROLES_CACHE_KEY);
            }
        }
        return row;
    }

    /**
     * 新增角色菜单信息
     * 
     * @param role 角色对象
     */
    public int insertRoleMenu(SysRole role) {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenuEntity> list = new ArrayList<>();
        for (Long menuId : role.getMenuIds()) {

            SysRoleMenuEntity rm = new SysRoleMenuEntity();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (!list.isEmpty()) {
            roleMenuMapper.insertRoleMenus(list);
            rows = list.size();
        }
        return rows;
    }

    /**
     * 批量删除角色信息
     * 
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Transactional
    public int deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            SysRoleEntity role = roleMapper.selectById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {
                throw ExceptionUtil.business(ErrorCodeEnums.ROLE_ASSIGNED_DELETE, role.getRoleName());
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.deleteByRoleIds(roleIds);
        int row = roleMapper.deleteRolesByIds(roleIds);
        if (row > 0) {
            synchronized (roleCacheLoadLock) {
                roleCache.invalidate(ALL_ROLES_CACHE_KEY);
            }
        }
        return row;
    }

    /**
     * 取消授权用户角色
     * 
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    public int deleteAuthUser(Long userId, Long roleId) {
        return userRoleMapper.deleteByUserAndRole(userId, roleId);
    }

    /**
     * 批量取消授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    public int deleteAuthUsers(Long roleId, Long[] userIds) {
        return userRoleMapper.deleteByRoleAndUsers(roleId, userIds);
    }

    /**
     * 批量选择授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    public int insertAuthUsers(Long roleId, Long[] userIds) {
        // 新增用户与角色管理
        List<SysUserRoleEntity> list = new ArrayList<>();
        for (Long userId : userIds) {
            SysUserRoleEntity ur = new SysUserRoleEntity();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        userRoleMapper.insertUserRoles(list);
        return list.size();
    }
}
