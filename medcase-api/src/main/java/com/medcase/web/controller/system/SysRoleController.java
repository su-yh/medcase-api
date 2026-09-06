package com.medcase.web.controller.system;

import com.medcase.common.annotation.Log;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.BusinessType;
import com.medcase.framework.web.service.SysPermissionService;
import com.medcase.framework.web.service.TokenService;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.service.SysRoleService;
import com.medcase.system.service.SysUserService;
import com.medcase.web.controller.system.dto.RoleAddRequest;
import com.medcase.web.controller.system.dto.RoleEditRequest;
import com.medcase.web.controller.system.dto.RoleMenuUpdateRequest;
import com.medcase.web.controller.system.dto.RoleQueryRequest;
import com.medcase.web.controller.system.dto.RoleStatusRequest;
import com.medcase.web.controller.system.dto.RoleUserRequest;
import com.medcase.web.controller.system.dto.UserQueryRequest;
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

import java.util.List;

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

    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public PageResult<SysRoleEntity> list(
            PageParam pageParam,
            RoleQueryRequest request,
            @CurrLoginUser LoginUser loginUser) {

        return roleService.selectPage(
                pageParam, request, loginUser.getUserId(), loginUser.getUser().isAdmin());
    }

    /**
     * 根据角色编号获取详细信息
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:query')")
    @GetMapping(value = "/{roleId}")
    public SysRoleEntity getInfo(
            @PathVariable Long roleId,
            @CurrLoginUser LoginUser loginUser) {

        return roleService.selectRoleById(
                roleId, loginUser.getUserId(), loginUser.getUser().isAdmin());
    }

    /**
     * 查询角色关联的菜单ID。
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:query')")
    @GetMapping("/{roleId}/menuIds")
    public List<Long> getRoleMenuIds(
            @PathVariable Long roleId,
            @CurrLoginUser LoginUser loginUser) {
        return roleService.selectRoleMenuIds(
                roleId, loginUser.getUserId(), loginUser.getUser().isAdmin());
    }

    /**
     * 新增角色
     */
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(
            @Validated @RequestBody RoleAddRequest request) {

        if (roleService.insertRole(request) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.OPERATION_FAILED);
        }

    }

    /**
     * 修改保存角色
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(
            @Validated @RequestBody RoleEditRequest request,
            @CurrLoginUser LoginUser loginUser) {

        if (roleService.updateRole(
                request, loginUser.getUserId(), loginUser.getUser().isAdmin()) > 0) {

            // 刷新所有持有该角色的在线用户权限
            tokenService.refreshPermissionByRoleId(request.getRoleId(), permissionService);
            return;
        }
        throw ExceptionUtil.business(ErrorCodeEnums.ROLE_UPDATE_FAILED);
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public void changeStatus(
            @Validated @RequestBody RoleStatusRequest request,
            @CurrLoginUser LoginUser loginUser) {

        if (roleService.updateRoleStatus(
                request, loginUser.getUserId(), loginUser.getUser().isAdmin()) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_STATUS_UPDATE_FAILED);
        }
    }

    /**
     * 修改角色关联菜单。
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:edit')")
    @Log(title = "角色菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping("/{roleId}/menus")
    public void updateRoleMenus(
            @PathVariable Long roleId,
            @Validated @RequestBody RoleMenuUpdateRequest request,
            @CurrLoginUser LoginUser loginUser) {
        if (roleService.updateRoleMenus(
                roleId, request, loginUser.getUserId(),
                loginUser.getUser().isAdmin(), loginUser.getUserId()) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.OPERATION_FAILED);
        }
    }

    /**
     * 删除角色
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:remove')")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public void remove(
            @PathVariable Long[] roleIds,
            @CurrLoginUser LoginUser loginUser) {

        if (roleService.deleteRoleByIds(
                roleIds, loginUser.getUserId(), loginUser.getUser().isAdmin()) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_DELETE_FAILED);
        }
    }

    /**
     * 获取角色选择框列表
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:query')")
    @GetMapping("/optionselect")
    public List<SysRoleEntity> optionselect(
            @CurrLoginUser LoginUser loginUser) {

        return roleService.selectRoleAll(
                loginUser.getUserId(), loginUser.getUser().isAdmin());
    }

    /**
     * 查询已分配用户角色列表
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/allocatedList")
    public PageResult<SysUserEntity> allocatedList(
            PageParam pageParam,
            UserQueryRequest user,
            @CurrLoginUser LoginUser loginUser) {

        roleService.selectRoleById(
                user.getRoleId(), loginUser.getUserId(), loginUser.getUser().isAdmin());
        return userService.selectAllocatedPage(user, pageParam);
    }

    /**
     * 查询未分配用户角色列表
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:list')")
    @GetMapping("/authUser/unallocatedList")
    public PageResult<SysUserEntity> unallocatedList(
            PageParam pageParam,
            UserQueryRequest user,
            @CurrLoginUser LoginUser loginUser) {

        roleService.selectRoleById(
                user.getRoleId(), loginUser.getUserId(), loginUser.getUser().isAdmin());
        return userService.selectUnallocatedPage(user, pageParam);
    }

    /**
     * 取消授权用户
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public void cancelAuthUser(
            @RequestBody RoleUserRequest request,
            @CurrLoginUser LoginUser loginUser) {

        if (roleService.deleteAuthUser(
                request.getUserId(), request.getRoleId(),
                loginUser.getUserId(), loginUser.getUser().isAdmin()) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_AUTH_USER_DELETE_FAILED);
        }
    }

    /**
     * 批量取消授权用户
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public void cancelAuthUserAll(
            Long roleId,
            Long[] userIds,
            @CurrLoginUser LoginUser loginUser) {

        if (roleService.deleteAuthUsers(
                roleId, userIds, loginUser.getUserId(), loginUser.getUser().isAdmin()) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_AUTH_USER_DELETE_FAILED);
        }
    }

    /**
     * 批量选择用户授权
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:role:edit')")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public void selectAuthUserAll(
            Long roleId,
            Long[] userIds,
            @CurrLoginUser LoginUser loginUser) {

        if (roleService.insertAuthUsers(
                roleId, userIds, loginUser.getUserId(), loginUser.getUser().isAdmin()) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.ROLE_AUTH_USER_SELECT_FAILED);
        }
    }

}
