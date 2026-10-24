package com.medcase.web.controller.system;

import com.medcase.common.core.domain.TreeSelect;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.audit.AuditOperation;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.service.SysDeptService;
import com.medcase.system.service.SysPostService;
import com.medcase.system.service.SysRoleService;
import com.medcase.system.service.SysUserService;
import com.medcase.web.controller.system.dto.DeptQueryRequest;
import com.medcase.web.controller.system.dto.UserAuthRoleResponse;
import com.medcase.web.controller.system.dto.UserDetailResponse;
import com.medcase.web.controller.system.dto.UserQueryRequest;
import com.medcase.web.controller.system.dto.UserResetPasswordRequest;
import com.medcase.web.controller.system.dto.UserSaveRequest;
import com.medcase.web.controller.system.dto.UserStatusRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息
 * 
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

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
    public PageResult<SysUserEntity> list(
            PageParam pageParam,
            UserQueryRequest user,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime) {

        return userService.selectPage(user, pageParam, beginTime, endTime);
    }

    /**
     * 根据用户编号获取用户详细信息（含该用户已关联的岗位、角色 ID）。
     * <p>新增/修改用户表单的「岗位」「角色」下拉选项，请分别调用
     * {@code GET /system/post/optionselect} 与 {@code GET /system/role/optionselect}，
     * 本接口不再返回全量岗位/角色列表。
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{userId}")
    public UserDetailResponse getInfo(@PathVariable Long userId) {
        SysUserEntity sysUser = userService.selectUserById(userId);
        List<Long> postIds = postService.selectPostListByUserId(userId);
        List<SysRoleEntity> roles = sysUser.getRoles();
        List<Long> roleIds = roles == null ? null
                : roles.stream().map(SysRoleEntity::getRoleId).collect(Collectors.toList());
        return new UserDetailResponse(sysUser, postIds, roleIds);
    }

    /**
     * 新增用户
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).CREATE_ADMIN_USER, " +
            "#spelReturnValue, #request, #loginUser, #user)")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @PostMapping
    public void add(
            HttpServletRequest request,
            @CurrLoginUser LoginUser loginUser,
            @Validated @RequestBody UserSaveRequest user) {

        if (!userService.checkUserNameUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.USERNAME_EXISTS, user.getUserName());
        }
        else if (StringUtils.hasText(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.PHONE_EXISTS, user.getUserName());
        }
        else if (StringUtils.hasText(user.getEmail()) && !userService.checkEmailUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.EMAIL_EXISTS, user.getUserName());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userService.insertUser(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_OPERATION_FAILED);
        }
    }

    /**
     * 修改用户
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).UPDATE_ADMIN_USER, " +
            "#spelReturnValue, #request, #loginUser, #user)")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping
    public void edit(
            HttpServletRequest request,
            @CurrLoginUser LoginUser loginUser,
            @Validated @RequestBody UserSaveRequest user) {

        user.setPassword(null);

        userService.checkUserAllowed(user.getUserId());
        if (!userService.checkUserNameUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.USERNAME_EXISTS, user.getUserName());
        }
        else if (StringUtils.hasText(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.PHONE_EXISTS, user.getUserName());
        }
        else if (StringUtils.hasText(user.getEmail()) && !userService.checkEmailUnique(user)) {
            throw ExceptionUtil.business(ErrorCodeEnums.EMAIL_EXISTS, user.getUserName());
        }
        if (userService.updateUser(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_OPERATION_FAILED);
        }
    }

    /**
     * 删除用户
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).DELETE_ADMIN_USER, " +
            "#spelReturnValue, #request, #loginUser, #userIds)")
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public void remove(
            HttpServletRequest request,
            @PathVariable Long[] userIds,
            @CurrLoginUser LoginUser loginUser) {

        if (ArrayUtils.contains(userIds, loginUser.getUserId())) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_CANNOT_DELETE_SELF);
        }
        if (userService.deleteUserByIds(userIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_OPERATION_FAILED);
        }
    }

    /**
     * 重置密码
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).RESET_ADMIN_USER_PASSWORD, " +
            "#spelReturnValue, #request, #loginUser, #user)")
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public void resetPwd(
            HttpServletRequest request,
            @Validated @RequestBody UserResetPasswordRequest user,
            @CurrLoginUser LoginUser loginUser) {

        userService.checkUserAllowed(user.getUserId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userService.resetPwd(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_OPERATION_FAILED);
        }
    }

    /**
     * 状态修改
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).UPDATE_ADMIN_USER_STATUS, " +
            "#spelReturnValue, #request, #loginUser, #user)")
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN) " +
            "&& @ss.hasPermi('system:user:edit')")
    @PutMapping("/changeStatus")
    public void changeStatus(
            HttpServletRequest request,
            @Validated @RequestBody UserStatusRequest user,
            @CurrLoginUser LoginUser loginUser) {

        userService.checkUserAllowed(user.getUserId());
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

        SysUserEntity user = userService.selectUserById(userId);
        List<SysRoleEntity> roles = roleService.selectRolesByUserId(userId);
        return new UserAuthRoleResponse(user, roles);
    }

    /**
     * 用户授权角色
     */
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).UPDATE_ADMIN_USER_ROLES, " +
            "#spelReturnValue, #request, #loginUser, #userId, #roleIds)")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping("/authRole")
    public void insertAuthRole(
            HttpServletRequest request,
            @CurrLoginUser LoginUser loginUser,
            Long userId,
            Long[] roleIds) {

        userService.insertUserAuth(userId, roleIds);
    }

    /**
     * 获取部门树列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/deptTree")
    public List<TreeSelect> deptTree(DeptQueryRequest request) {

        return deptService.selectDeptTreeList(request);
    }
}
