package com.medcase.web.controller.system;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Log;
import com.medcase.common.core.controller.BaseController;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.BusinessType;
import com.medcase.common.utils.DateUtils;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.common.utils.StringUtils;
import com.medcase.framework.web.service.TokenService;
import com.medcase.system.service.ISysUserService;
import com.medcase.web.controller.system.dto.ProfileResponse;

/**
 * 个人信息 业务处理
 * 
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 个人信息
     */
    @GetMapping
    public ProfileResponse profile() {

        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        return new ProfileResponse(
                user,
                userService.selectUserRoleGroup(loginUser.getUsername()),
                userService.selectUserPostGroup(loginUser.getUsername()));
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public void updateProfile(@RequestBody SysUser user) {

        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();
        currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser)) {
            throw ExceptionUtil.business(ErrorCodeEnums.PROFILE_PHONE_EXISTS, loginUser.getUsername());
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser)) {
            throw ExceptionUtil.business(ErrorCodeEnums.PROFILE_EMAIL_EXISTS, loginUser.getUsername());
        }
        if (userService.updateUserProfile(currentUser) > 0) {

            // 更新缓存用户信息
            tokenService.setLoginUser(loginUser);
            return;
        }
        throw ExceptionUtil.business(ErrorCodeEnums.PROFILE_UPDATE_FAILED);
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public void updatePwd(@RequestBody Map<String, String> params) {

        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        LoginUser loginUser = getLoginUser();
        Long userId = loginUser.getUserId();
        SysUser user = userService.selectUserById(userId);
        String password = user.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            throw ExceptionUtil.business(ErrorCodeEnums.PROFILE_OLD_PASSWORD_INVALID);
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            throw ExceptionUtil.business(ErrorCodeEnums.PROFILE_PASSWORD_SAME);
        }
        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (userService.resetUserPwd(userId, newPassword) > 0) {

            // 更新缓存用户密码&密码最后更新时间
            loginUser.getUser().setPwdUpdateDate(DateUtils.getNowDate());
            loginUser.getUser().setPassword(newPassword);
            tokenService.setLoginUser(loginUser);
            return;
        }
        throw ExceptionUtil.business(ErrorCodeEnums.PROFILE_PASSWORD_UPDATE_FAILED);
    }

}
