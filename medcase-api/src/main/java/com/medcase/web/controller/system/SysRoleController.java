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
import com.medcase.common.core.controller.BaseController;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.enums.BusinessType;
import com.medcase.framework.web.service.SysPermissionService;
import com.medcase.framework.web.service.TokenService;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.domain.SysUserRole;
import com.medcase.system.service.ISysDeptService;
import com.medcase.system.service.ISysRoleService;
import com.medcase.system.service.ISysUserService;
import com.medcase.web.controller.system.dto.RoleDeptTreeResponse;

/**
 * 角色信息
 * 
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public PageResult<SysRole> list(SysRole role) {

        startPage();
        List<SysRole> list = roleService.selectRoleList(role);
        return getPageResult(list);
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public SysRole getInfo(@PathVariable Long roleId) {

        roleService.checkRoleDataScope(roleId);
        return roleService.selectRoleById(roleId);
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysRole role) {

        if (!roleService.checkRoleNameUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_NAME_EXISTS);
        }
        else if (!roleService.checkRoleKeyUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_KEY_EXISTS);
        }
        role.setCreateBy(getUsername());
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
    public void edit(@Validated @RequestBody SysRole role) {

        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (!roleService.checkRoleNameUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_NAME_EXISTS);
        }
        else if (!roleService.checkRoleKeyUnique(role)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_KEY_EXISTS);
        }
        role.setUpdateBy(getUsername());
        
        if (roleService.updateRole(role) > 0) {

            // 刷新所有持有该角色的在线用户权限
            tokenService.refreshPermissionByRoleId(role.getRoleId(), permissionService);
            return;
        }
        throw ExceptionUtil.business(ErrorCodeEnums.ROLE_UPDATE_FAILED);
    }

    /**
     * 修改保存数据权限
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public void dataScope(@RequestBody SysRole role) {

        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (roleService.authDataScope(role) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_DATA_SCOPE_UPDATE_FAILED);
        }
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public void changeStatus(@RequestBody SysRole role) {

        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        role.setUpdateBy(getUsername());
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
    public PageResult<SysUser> allocatedList(SysUser user) {

        startPage();
        List<SysUser> list = userService.selectAllocatedList(user);
        return getPageResult(list);
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public PageResult<SysUser> unallocatedList(SysUser user) {

        startPage();
        List<SysUser> list = userService.selectUnallocatedList(user);
        return getPageResult(list);
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public void cancelAuthUser(@RequestBody SysUserRole userRole) {

        if (roleService.deleteAuthUser(userRole) <= 0) {
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

        roleService.checkRoleDataScope(roleId);
        if (roleService.insertAuthUsers(roleId, userIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_AUTH_USER_SELECT_FAILED);
        }
    }

    /**
     * 获取对应角色部门树列表
     */
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping(value = "/deptTree/{roleId}")
    public RoleDeptTreeResponse deptTree(@PathVariable("roleId") Long roleId) {

        return new RoleDeptTreeResponse(
                deptService.selectDeptListByRoleId(roleId),
                deptService.selectDeptTreeList(new SysDept()));
    }
}
