package com.medcase.framework.web.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.common.constant.CacheConstants;
import com.medcase.common.constant.Constants;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.core.redis.RedisCache;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.DateUtils;
import com.medcase.common.utils.MessageUtils;
import com.medcase.common.utils.ip.IpUtils;
import com.medcase.framework.manager.AsyncManager;
import com.medcase.framework.manager.factory.AsyncFactory;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.service.SysConfigService;
import com.medcase.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 用户登录服务。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {
    private final TokenService tokenService;

    private final RedisCache redisCache;

    private final SysUserService userService;

    private final SysConfigService configService;

    private final SysPasswordService passwordService;

    private final UserDetailsServiceImpl userDetailsService;

    private final UserMapper userMapper;

    private final SysPermissionService permissionService;

    private final PasswordEncoder passwordEncoder;

    public String login(String username, String password, String code, String uuid, UserTypeEnums userType) {
        validateCaptcha(username, code, uuid);
        loginPreCheck(username, password);

        LoginUser loginUser = userType == UserTypeEnums.ADMIN
                ? loginAdmin(username, password)
                : loginPortalUser(username, password, userType);

        updateLoginInfo(loginUser.getUserId(), userType);
        return tokenService.createToken(loginUser);
    }

    public void validateCaptcha(String username, String code, String uuid) {
        if (!configService.selectCaptchaEnabled()) {
            return;
        }

        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + com.medcase.common.utils.StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        if (captcha == null) {
            AsyncManager.me().execute(
                    AsyncFactory.recordLogininfor(
                            username, Constants.LOGIN_FAIL, ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_EXPIRED.getMsg()));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_EXPIRED);
        }
        redisCache.deleteObject(verifyKey);
        if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(
                    AsyncFactory.recordLogininfor(
                            username, Constants.LOGIN_FAIL, ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_INVALID.getMsg()));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_INVALID);
        }
    }

    public void loginPreCheck(String username, String password) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            AsyncManager.me().execute(
                    AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_PARAMETER_EMPTY);
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(
                    username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(
                    username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
        }
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            AsyncManager.me().execute(
                    AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_IP_BLOCKED);
        }
    }

    private LoginUser loginAdmin(String username, String password) {
        passwordService.validateLoginRetryCount(username);
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            LoginUser loginUser = (LoginUser) userDetails;
            if (!passwordEncoder.matches(password, loginUser.getPassword())) {
                passwordService.recordLoginFailure(username);
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(
                        username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
            }

            passwordService.clearLoginRecordCache(username);
            AsyncManager.me().execute(
                    AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
            return loginUser;
        } catch (RuntimeException e) {
            if (e instanceof com.medcase.mvc.exception.AbstractBusinessException) {
                throw e;
            }

            String message = resolveExceptionMessage(e);
            log.warn("admin login authentication error, username={}", username, e);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, message));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_AUTHENTICATION_ERROR, message);
        }
    }

    private LoginUser loginPortalUser(String username, String password, UserTypeEnums userType) {
        UserEntity user = userMapper.selectUserByUsername(username, userType);
        if (user == null) {
            log.warn("portal login failed, user not exists, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_USER_NOT_EXISTS);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("portal login failed, password mismatch, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_FAILED);
        }
        if (user.getStatus() == UserStatusEnums.DISABLE) {
            log.warn("portal login failed, user disabled, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_FAILED);
        }

        SysUser sysUser = toSysUser(user);
        return new LoginUser(
                sysUser.getUserId(), null, sysUser, permissionService.getMenuPermission(sysUser));
    }

    private void updateLoginInfo(Long userId, UserTypeEnums userType) {
        if (userType == UserTypeEnums.ADMIN) {
            userService.updateLoginInfo(userId, IpUtils.getIpAddr(), DateUtils.getNowDate());
            return;
        }

        UserEntity updateUser = new UserEntity();
        updateUser.setUserId(userId);
        updateUser.setLoginIp(IpUtils.getIpAddr());
        updateUser.setLoginDate(DateUtils.getNowDate());
        userMapper.updateById(updateUser);
    }

    private SysUser toSysUser(UserEntity user) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(user.getUserId());
        sysUser.setUserName(user.getUserName());
        sysUser.setNickName(user.getNickName());
        sysUser.setSex(user.getSex());
        sysUser.setUserType(user.getUserType());
        sysUser.setPassword(user.getPassword());
        sysUser.setStatus(user.getStatus().getCode());
        sysUser.setDelFlag(user.getDelFlag());
        sysUser.setLoginIp(user.getLoginIp());
        sysUser.setLoginDate(user.getLoginDate());
        sysUser.setPwdUpdateDate(user.getPwdUpdateDate());
        return sysUser;
    }

    private String resolveExceptionMessage(Throwable throwable) {
        if (StringUtils.hasText(throwable.getMessage())) {
            return throwable.getMessage();
        }
        return throwable.getClass().getSimpleName();
    }
}
