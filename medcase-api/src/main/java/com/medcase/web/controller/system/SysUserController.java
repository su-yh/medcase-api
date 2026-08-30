package com.medcase.web.controller.system;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
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
import com.medcase.mvc.response.R;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.page.TableDataInfo;
import com.medcase.common.enums.BusinessType;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.common.utils.StringUtils;
import com.medcase.system.service.ISysDeptService;
import com.medcase.system.service.ISysPostService;
import com.medcase.system.service.ISysRoleService;
import com.medcase.system.service.ISysUserService;
import com.medcase.web.controller.system.dto.UserAuthRoleResponse;
import com.medcase.web.controller.system.dto.UserDetailResponse;

/**
 * 用户信息
 * 
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysDeptService deptService;

    @Autowired
    private ISysPostService postService;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysUser user) {

        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = { "/", "/{userId}" })
    public R<UserDetailResponse> getInfo(@PathVariable(value = "userId", required = false) Long userId) {

        SysUser sysUser = null;
        List<Long> postIds = null;
        List<Long> roleIds = null;
        if (StringUtils.isNotNull(userId)) {

            userService.checkUserDataScope(userId);
            sysUser = userService.selectUserById(userId);
            postIds = postService.selectPostListByUserId(userId);
            roleIds = sysUser.getRoles().stream().map(SysRole::getRoleId).collect(Collectors.toList());
        }
        List<SysRole> roles = roleService.selectRoleAll();
        List<SysRole> availableRoles = SecurityUtils.isAdmin(userId)
                ? roles
                : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList());
        return R.ofSuccess(new UserDetailResponse(
                sysUser,
                postIds,
                roleIds,
                availableRoles,
                postService.selectPostAll()));
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysUser user) {

        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user)) {

            return R.ofFail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {

            return R.ofFail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {

            return R.ofFail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setCreateBy(getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return userService.insertUser(user) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysUser user) {

        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user)) {

            return R.ofFail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {

            return R.ofFail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {

            return R.ofFail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(getUsername());
        return userService.updateUser(user) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {

        if (ArrayUtils.contains(userIds, getUserId())) {

            return R.ofFail("当前用户不能删除");
        }
        return userService.deleteUserByIds(userIds) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUser user) {

        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(getUsername());
        return userService.resetPwd(user) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUser user) {

        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        return userService.updateUserStatus(user) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public R<UserAuthRoleResponse> authRole(@PathVariable("userId") Long userId) {

        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        List<SysRole> availableRoles = SecurityUtils.isAdmin(userId)
                ? roles
                : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList());
        return R.ofSuccess(new UserAuthRoleResponse(user, availableRoles));
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public R<Void> insertAuthRole(Long userId, Long[] roleIds) {

        userService.checkUserDataScope(userId);
        roleService.checkRoleDataScope(roleIds);
        userService.insertUserAuth(userId, roleIds);
        return R.ofSuccess();
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public R<List<com.medcase.common.core.domain.TreeSelect>> deptTree(SysDept dept) {

        return R.ofSuccess(deptService.selectDeptTreeList(dept));
    }
}
