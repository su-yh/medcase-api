package com.medcase.system.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.entity.SysMenu;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.entity.SysRoleMenuEntity;
import com.medcase.system.entity.SysUserRoleEntity;
import com.medcase.system.mapper.SysRoleMapper;
import com.medcase.system.mapper.SysRoleMenuMapper;
import com.medcase.system.mapper.SysUserRoleMapper;
import com.medcase.web.controller.system.dto.RoleAddRequest;
import com.medcase.web.controller.system.dto.RoleEditRequest;
import com.medcase.web.controller.system.dto.RoleMenuUpdateRequest;
import com.medcase.web.controller.system.dto.RoleQueryRequest;
import com.medcase.web.controller.system.dto.RoleStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

    @Autowired
    private SysMenuService menuService;

    private final Cache<String, List<SysRoleEntity>> roleCache = Caffeine.newBuilder()
            .expireAfterWrite(ROLE_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .build();

    private final Object roleCacheLoadLock = new Object();

    /**
     * 根据条件分页查询角色数据
     * @return 角色数据集合信息
     */
    public PageResult<SysRoleEntity> selectPage(
            PageParam pageParam, RoleQueryRequest request, String createBy, boolean admin) {
        return roleMapper.selectPage(pageParam, request, admin ? null : createBy);
    }

    /**
     * 根据用户ID查询角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<SysRoleEntity> selectRolesByUserId(Long userId) {
        List<SysRoleEntity> userRoles = selectRoleList(userId);
        List<SysRoleEntity> roles = selectRoleAll();
        if (userRoles == null) {
            return roles;
        }

        for (SysRoleEntity role : roles) {
            for (SysRoleEntity userRole : userRoles) {
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
        List<SysRoleEntity> perms = selectRoleList(userId);
        Set<String> permsSet = new HashSet<>();
        if (perms == null) {
            return permsSet;
        }

        for (SysRoleEntity perm : perms) {
            if (perm != null) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    private List<SysRoleEntity> selectRoleList(Long userId) {
        List<SysUserRoleEntity> userRoleEntities = userRoleMapper.selectByUserId(userId);
        if (userRoleEntities == null || userRoleEntities.isEmpty()) {
            return null;
        }

        List<SysRoleEntity> entities = new ArrayList<>();
        for (SysUserRoleEntity userRoleEntity : userRoleEntities) {
            SysRoleEntity sysRoleEntity = selectRoleById(userRoleEntity.getRoleId());
            entities.add(sysRoleEntity);
        }

        return entities;
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
     * 查询当前管理员允许管理的角色。
     *
     * @param roleId 角色ID
     * @param createBy 当前管理员账号
     * @param admin 是否为超级管理员
     * @return 角色对象信息
     */
    public SysRoleEntity selectRoleById(Long roleId, String createBy, boolean admin) {
        SysRoleEntity role = selectRoleById(roleId);
        if (role == null || (!admin && !Objects.equals(createBy, role.getCreateBy()))) {
            throw ExceptionUtil.business(ErrorCodeEnums.ACCESS_DENIED);
        }
        return role;
    }

    /**
     * 查询当前管理员允许分配的角色。
     *
     * @param createBy 当前管理员账号
     * @param admin 是否为超级管理员
     * @return 角色列表
     */
    public List<SysRoleEntity> selectRoleAll(String createBy, boolean admin) {
        List<SysRoleEntity> roles = selectRoleAll();
        if (admin) {
            return roles;
        }
        return roles.stream()
                .filter(role -> Objects.equals(createBy, role.getCreateBy()))
                .toList();
    }

    /**
     * 校验角色名称是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    private boolean checkRoleNameUnique(SysRoleEntity role) {
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
    private boolean checkRoleKeyUnique(SysRoleEntity role) {
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
     * @param request 角色新增请求
     * @param createBy 创建人
     * @return 结果
     */
    @Transactional
    public int insertRole(RoleAddRequest request, String createBy) {
        SysRoleEntity role = new SysRoleEntity();
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRoleSort(request.getRoleSort());
        role.setMenuCheckStrictly(request.isMenuCheckStrictly());
        role.setStatus(request.getStatus());
        role.setRemark(request.getRemark());
        role.setCreateBy(createBy);

        if (!checkRoleNameUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_NAME_EXISTS);
        }
        if (!checkRoleKeyUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_KEY_EXISTS);
        }

        int row = roleMapper.insert(role);
        if (row > 0) {
            synchronized (roleCacheLoadLock) {
                roleCache.invalidate(ALL_ROLES_CACHE_KEY);
            }
        }
        return row;
    }

    /**
     * 修改保存角色信息
     * 
     * @param request 角色修改请求
     * @param updateBy 更新人
     * @return 结果
     */
    @Transactional
    public int updateRole(RoleEditRequest request, String updateBy, boolean admin) {
        selectRoleById(request.getRoleId(), updateBy, admin);
        SysRoleEntity role = new SysRoleEntity();
        role.setRoleId(request.getRoleId());
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRoleSort(request.getRoleSort());
        role.setMenuCheckStrictly(request.isMenuCheckStrictly());
        role.setStatus(request.getStatus());
        role.setRemark(request.getRemark());
        role.setUpdateBy(updateBy);

        if (!checkRoleNameUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_NAME_EXISTS);
        }
        if (!checkRoleKeyUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_KEY_EXISTS);
        }

        int row = roleMapper.updateById(role);
        if (row > 0) {
            synchronized (roleCacheLoadLock) {
                roleCache.invalidate(ALL_ROLES_CACHE_KEY);
            }
        }
        return row;
    }

    /**
     * 修改角色状态
     * 
     * @param request 角色状态修改请求
     * @param updateBy 更新人
     * @return 结果
     */
    public int updateRoleStatus(RoleStatusRequest request, String updateBy, boolean admin) {
        selectRoleById(request.getRoleId(), updateBy, admin);
        SysRoleEntity role = new SysRoleEntity();
        role.setRoleId(request.getRoleId());
        role.setStatus(request.getStatus());
        role.setUpdateBy(updateBy);

        int row = roleMapper.updateById(role);
        if (row > 0) {
            synchronized (roleCacheLoadLock) {
                roleCache.invalidate(ALL_ROLES_CACHE_KEY);
            }
        }
        return row;
    }

    /**
     * 查询角色关联的菜单ID。
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    public List<Long> selectRoleMenuIds(Long roleId, String createBy, boolean admin) {
        selectRoleById(roleId, createBy, admin);
        return roleMenuMapper.selectMenuIdsByRoleId(roleId);
    }

    /**
     * 修改角色关联菜单。
     *
     * @param roleId 角色ID
     * @param request 角色菜单关联修改请求
     * @return 结果
     */
    @Transactional
    public int updateRoleMenus(
            Long roleId, RoleMenuUpdateRequest request,
            String createBy, boolean admin, Long userId) {
        selectRoleById(roleId, createBy, admin);
        if (!admin) {
            List<SysMenu> menus = menuService.selectMenuList(userId);
            Set<Long> menuIds = new HashSet<>();
            Long[] requestedMenuIds = request.getMenuIds();
            if (menus != null) {
                for (SysMenu menu : menus) {
                    if (menu != null && menu.getMenuId() != null) {
                        menuIds.add(menu.getMenuId());
                    }
                }
            }
            if (requestedMenuIds != null) {
                for (Long menuId : requestedMenuIds) {
                    if (!menuIds.contains(menuId)) {
                        throw ExceptionUtil.business(ErrorCodeEnums.ACCESS_DENIED);
                    }
                }
            }
        }
        SysRoleEntity role = new SysRoleEntity();
        role.setRoleId(roleId);
        role.setMenuCheckStrictly(request.isMenuCheckStrictly());
        int row = roleMapper.updateById(role);
        if (row <= 0) {
            return row;
        }

        roleMenuMapper.deleteByRoleId(roleId);
        List<SysRoleMenuEntity> list = new ArrayList<>();
        Long[] menuIds = request.getMenuIds();
        if (menuIds == null) {
            return row;
        }
        for (Long menuId : menuIds) {

            SysRoleMenuEntity rm = new SysRoleMenuEntity();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (!list.isEmpty()) {
            roleMenuMapper.insertRoleMenus(list);
            row = list.size();
        }
        synchronized (roleCacheLoadLock) {
            roleCache.invalidate(ALL_ROLES_CACHE_KEY);
        }
        return row;
    }

    /**
     * 批量删除角色信息
     * 
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Transactional
    public int deleteRoleByIds(Long[] roleIds, String createBy, boolean admin) {
        for (Long roleId : roleIds) {
            SysRoleEntity role = selectRoleById(roleId, createBy, admin);
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
    public int deleteAuthUser(
            Long userId, Long roleId, String createBy, boolean admin) {
        selectRoleById(roleId, createBy, admin);
        return userRoleMapper.deleteByUserAndRole(userId, roleId);
    }

    /**
     * 批量取消授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    public int deleteAuthUsers(
            Long roleId, Long[] userIds, String createBy, boolean admin) {
        selectRoleById(roleId, createBy, admin);
        return userRoleMapper.deleteByRoleAndUsers(roleId, userIds);
    }

    /**
     * 批量选择授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    public int insertAuthUsers(
            Long roleId, Long[] userIds, String createBy, boolean admin) {
        selectRoleById(roleId, createBy, admin);
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
