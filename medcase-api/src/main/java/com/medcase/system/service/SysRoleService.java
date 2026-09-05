package com.medcase.system.service;

import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.utils.spring.SpringUtils;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.entity.SysRoleMenuEntity;
import com.medcase.system.entity.SysUserRoleEntity;
import com.medcase.system.mapper.SysRoleMapper;
import com.medcase.system.mapper.SysRoleMenuMapper;
import com.medcase.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色 业务层处理
 * 
 */
@Service
public class SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    /**
     * 根据条件分页查询角色数据
     * 
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    public List<SysRole> selectRoleList(SysRole role) {

        return roleMapper.selectRoleList(role);
    }

    /**
     * 根据用户ID查询角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    public List<SysRole> selectRolesByUserId(Long userId) {

        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles) {

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
    public List<SysRole> selectRoleAll() {

        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole());
    }

    /**
     * 通过角色ID查询角色
     * 
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    public SysRole selectRoleById(Long roleId) {

        return SystemEntityConverter.toDomain(roleMapper.selectById(roleId));
    }

    /**
     * 校验角色名称是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    public boolean checkRoleNameUnique(SysRole role) {

        Long roleId = role.getRoleId() == null ? -1L : role.getRoleId();
        SysRole info = SystemEntityConverter.toDomain(
                roleMapper.selectRoleByName(role.getRoleName()));
        if (info != null && info.getRoleId().longValue() != roleId.longValue()) {

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

        Long roleId = role.getRoleId() == null ? -1L : role.getRoleId();
        SysRole info = SystemEntityConverter.toDomain(
                roleMapper.selectRoleByKey(role.getRoleKey()));
        if (info != null && info.getRoleId().longValue() != roleId.longValue()) {

            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     * 
     * @param role 角色信息
     */
    public void checkRoleAllowed(SysRole role) {

        if (role.getRoleId() != null && role.isAdmin()) {

            throw ExceptionUtil.business(ErrorCodeEnums.SUPER_ADMIN_ROLE_OPERATION);
        }
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
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     * 
     * @param role 角色信息
     * @return 结果
     */
    public int updateRoleStatus(SysRole role) {

        return roleMapper.updateById(SystemEntityConverter.toEntity(role));
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
        if (list.size() > 0) {

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

            checkRoleAllowed(new SysRole(roleId));
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0) {

                throw ExceptionUtil.business(ErrorCodeEnums.ROLE_ASSIGNED_DELETE, role.getRoleName());
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.deleteByRoleIds(roleIds);
        return roleMapper.deleteRolesByIds(roleIds);
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
