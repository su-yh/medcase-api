package com.medcase.web.controller.system;

import com.medcase.common.annotation.Log;
import com.medcase.common.core.controller.BaseController;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.enums.BusinessType;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.common.utils.StringUtils;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.service.SysDeptService;
import com.medcase.system.service.SysPostService;
import com.medcase.system.service.SysRoleService;
import com.medcase.system.service.SysUserService;
import com.medcase.web.controller.system.dto.PostResponse;
import com.medcase.web.controller.system.dto.UserAuthRoleResponse;
import com.medcase.web.controller.system.dto.UserDetailResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.stream.Collectors;

/**
 * 用户信息
 * 
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysDeptService deptService;

    @Autowired
    private SysPostService postService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public PageResult<SysUser> list(PageParam pageParam, SysUser user) {

        return userService.selectPage(user, pageParam);
    }

    /**
     * 获取新增用户所需的基础数据。
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/")
    public UserDetailResponse getNewUserInfo() {
        return buildUserDetailResponse(null);
    }

    /**
     * 根据用户编号获取详细信息。
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{userId}")
    public UserDetailResponse getInfo(@PathVariable Long userId) {
        return buildUserDetailResponse(userId);
    }

    private UserDetailResponse buildUserDetailResponse(Long userId) {
        SysUser sysUser = null;
        List<Long> postIds = null;
        List<Long> roleIds = null;
        if (StringUtils.isNotNull(userId)) {

            userService.checkUserDataScope(userId);
            sysUser = userService.selectUserById(userId);
            postIds = postService.selectPostListByUserId(userId);
            List<SysRole> roles = sysUser.getRoles();
            if (roles != null) {
                roleIds = roles.stream().map(SysRole::getRoleId).collect(Collectors.toList());
            }
        }
        List<SysRole> roles = roleService.selectRoleAll();
        List<SysRole> availableRoles = SecurityUtils.isAdmin(userId)
                ? roles
                : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList());
        List<PostResponse> posts = postService.selectPostAll().stream()
                .map(PostResponse::fromEntity)
                .toList();
        return new UserDetailResponse(sysUser, postIds, roleIds, availableRoles, posts);
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody SysUser user) {

        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.USERNAME_EXISTS, user.getUserName());
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.PHONE_EXISTS, user.getUserName());
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.EMAIL_EXISTS, user.getUserName());
        }
        user.setCreateBy(getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userService.insertUser(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_OPERATION_FAILED);
        }
    }

    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody SysUser user) {

        user.setPassword(null);

        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        deptService.checkDeptDataScope(user.getDeptId());
        roleService.checkRoleDataScope(user.getRoleIds());
        if (!userService.checkUserNameUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.USERNAME_EXISTS, user.getUserName());
        }
        else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.PHONE_EXISTS, user.getUserName());
        }
        else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.EMAIL_EXISTS, user.getUserName());
        }
        user.setUpdateBy(getUsername());
        if (userService.updateUser(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_OPERATION_FAILED);
        }
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public void remove(@PathVariable Long[] userIds) {

        if (ArrayUtils.contains(userIds, getUserId())) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_CANNOT_DELETE_SELF);
        }
        if (userService.deleteUserByIds(userIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_OPERATION_FAILED);
        }
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public void resetPwd(@RequestBody SysUser user) {

        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUpdateBy(getUsername());
        if (userService.resetPwd(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_OPERATION_FAILED);
        }
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public void changeStatus(@RequestBody SysUser user) {

        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getUserId());
        user.setUpdateBy(getUsername());
        if (userService.updateUserStatus(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_OPERATION_FAILED);
        }
    }

    /**
     * 根据用户编号获取授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/authRole/{userId}")
    public UserAuthRoleResponse authRole(@PathVariable("userId") Long userId) {

        SysUser user = userService.selectUserById(userId);
        List<SysRole> roles = roleService.selectRolesByUserId(userId);
        List<SysRole> availableRoles = SecurityUtils.isAdmin(userId)
                ? roles
                : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList());
        return new UserAuthRoleResponse(user, availableRoles);
    }

    /**
     * 用户授权角色
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public void insertAuthRole(Long userId, Long[] roleIds) {

        userService.checkUserDataScope(userId);
        roleService.checkRoleDataScope(roleIds);
        userService.insertUserAuth(userId, roleIds);
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public List<com.medcase.common.core.domain.TreeSelect> deptTree(SysDept dept) {

        return deptService.selectDeptTreeList(dept);
    }
}
