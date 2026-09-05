package com.medcase.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Log;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.enums.BusinessType;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.framework.web.service.SysPermissionService;
import com.medcase.framework.web.service.TokenService;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.service.SysRoleService;
import com.medcase.system.service.SysUserService;
import com.medcase.web.controller.system.dto.RoleQueryRequest;
import com.medcase.web.controller.system.dto.RoleUserRequest;

/**
 * 角色信息
 * 
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private SysUserService userService;

    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public PageResult<SysRoleEntity> list(
            PageParam pageParam,
            RoleQueryRequest request) {

        return roleService.selectPage(pageParam, request);
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public SysRole getInfo(@PathVariable Long roleId) {

        return roleService.selectRoleById(roleId);
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(
            @Validated @RequestBody SysRole role,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        if (!roleService.checkRoleNameUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_NAME_EXISTS);
        }
        else if (!roleService.checkRoleKeyUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_KEY_EXISTS);
        }
        role.setCreateBy(loginUser.getUsername());
        if (roleService.insertRole(role) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.OPERATION_FAILED);
        }

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(
            @Validated @RequestBody SysRole role,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        if (!roleService.checkRoleNameUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_NAME_EXISTS);
        }
        else if (!roleService.checkRoleKeyUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_KEY_EXISTS);
        }
        role.setUpdateBy(loginUser.getUsername());
        
        if (roleService.updateRole(role) > 0) {

            // 刷新所有持有该角色的在线用户权限
            tokenService.refreshPermissionByRoleId(role.getRoleId(), permissionService);
            return;
        }
        throw ExceptionUtil.business(ErrorCodeEnums.ROLE_UPDATE_FAILED);
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public void changeStatus(
            @RequestBody SysRole role,
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser loginUser) {

        role.setUpdateBy(loginUser.getUsername());
        if (roleService.updateRoleStatus(role) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_STATUS_UPDATE_FAILED);
        }
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public void remove(@PathVariable Long[] roleIds) {

        if (roleService.deleteRoleByIds(roleIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_DELETE_FAILED);
        }
    }

    /**
     * 获取角色选择框列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public List<SysRole> optionselect() {

        return roleService.selectRoleAll();
    }

    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public PageResult<SysUser> allocatedList(PageParam pageParam, SysUser user) {

        return userService.selectAllocatedPage(user, pageParam);
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public PageResult<SysUser> unallocatedList(PageParam pageParam, SysUser user) {

        return userService.selectUnallocatedPage(user, pageParam);
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public void cancelAuthUser(@RequestBody RoleUserRequest request) {

        if (roleService.deleteAuthUser(request.getUserId(), request.getRoleId()) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_AUTH_USER_DELETE_FAILED);
        }
    }

    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public void cancelAuthUserAll(Long roleId, Long[] userIds) {

        if (roleService.deleteAuthUsers(roleId, userIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_AUTH_USER_DELETE_FAILED);
        }
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public void selectAuthUserAll(Long roleId, Long[] userIds) {

        if (roleService.insertAuthUsers(roleId, userIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_AUTH_USER_SELECT_FAILED);
        }
    }

}
