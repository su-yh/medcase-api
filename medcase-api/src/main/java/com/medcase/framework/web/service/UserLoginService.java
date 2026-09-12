package com.medcase.framework.web.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.common.constant.CacheConstants;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.core.redis.RedisCache;
import com.medcase.common.enums.LoginRecordStatusEnums;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.ip.IpUtils;
import com.medcase.framework.manager.AsyncManager;
import com.medcase.framework.manager.factory.AsyncFactory;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.service.SysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用户登录服务。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {
    private final TokenService tokenService;

    private final RedisCache redisCache;

    private final SysConfigService configService;

    private final SysPasswordService passwordService;

    private final UserDetailsServiceImpl userDetailsService;

    private final UserMapper userMapper;

    private final SysPermissionService permissionService;

    private final PasswordEncoder passwordEncoder;

    public String login(String username, String password, String code, String uuid, UserTypeEnums userType) {
        validateCaptcha(username, code, uuid, userType);
        loginPreCheck(username, password, userType);

        LoginUser loginUser = userType == UserTypeEnums.ADMIN
                ? loginAdmin(username, password)
                : loginPortalUser(username, password, userType);

        return tokenService.createToken(loginUser);
    }

    public void validateCaptcha(
            String username, String code, String uuid, UserTypeEnums userType) {
        if (!configService.selectCaptchaEnabled()) {
            return;
        }

        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + (uuid == null ? "" : uuid);
        String captcha = redisCache.getCacheObject(verifyKey);
        if (captcha == null) {
            AsyncManager.me().execute(
                    AsyncFactory.recordLogin(username, null, userType,
                            LoginRecordStatusEnums.FAIL,
                            ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_EXPIRED.getMsg()));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_EXPIRED);
        }
        redisCache.deleteObject(verifyKey);
        if (!code.equalsIgnoreCase(captcha)) {
            AsyncManager.me().execute(
                    AsyncFactory.recordLogin(username, null, userType,
                            LoginRecordStatusEnums.FAIL,
                            ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_INVALID.getMsg()));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_INVALID);
        }
    }

    public void loginPreCheck(String username, String password, UserTypeEnums userType) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            AsyncManager.me().execute(
                    AsyncFactory.recordLogin(username, null, userType,
                            LoginRecordStatusEnums.FAIL,
                            ErrorCodeEnums.ADMIN_LOGIN_PARAMETER_EMPTY.getMsg()));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_PARAMETER_EMPTY);
        }
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogin(username, null, userType,
                    LoginRecordStatusEnums.FAIL, ErrorCodeEnums.ADMIN_LOGIN_FAILED.getMsg()));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
        }
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            AsyncManager.me().execute(AsyncFactory.recordLogin(username, null, userType,
                    LoginRecordStatusEnums.FAIL, ErrorCodeEnums.ADMIN_LOGIN_FAILED.getMsg()));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
        }
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {
            AsyncManager.me().execute(
                    AsyncFactory.recordLogin(username, null, userType,
                            LoginRecordStatusEnums.FAIL, ErrorCodeEnums.ADMIN_LOGIN_IP_BLOCKED.getMsg()));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_IP_BLOCKED);
        }
    }

    private LoginUser loginAdmin(String username, String password) {
        Long userId = null;
        try {
            passwordService.validateLoginRetryCount(username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            LoginUser loginUser = (LoginUser) userDetails;
            userId = loginUser.getUserId();
            if (!passwordEncoder.matches(password, loginUser.getPassword())) {
                passwordService.recordLoginFailure(username);
                throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
            }

            passwordService.clearLoginRecordCache(username);
            AsyncManager.me().execute(
                    AsyncFactory.recordLogin(username, userId, UserTypeEnums.ADMIN,
                            LoginRecordStatusEnums.SUCCESS, "登录成功"));
            return loginUser;
        }
        catch (AbstractBusinessException e) {
            AsyncManager.me().execute(AsyncFactory.recordLogin(
                    username, userId, UserTypeEnums.ADMIN,
                    LoginRecordStatusEnums.FAIL, e.getEc().getMsg()));
            throw e;
        }
        catch (RuntimeException e) {
            String message = resolveExceptionMessage(e);
            log.warn("admin login authentication error, username={}", username, e);
            AsyncManager.me().execute(AsyncFactory.recordLogin(
                    username, userId, UserTypeEnums.ADMIN,
                    LoginRecordStatusEnums.FAIL, message));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_AUTHENTICATION_ERROR, message);
        }
    }

    private LoginUser loginPortalUser(String username, String password, UserTypeEnums userType) {
        Long userId = null;
        try {
            UserEntity user = userMapper.selectUserByUsername(username, userType);
            if (user == null) {
                log.warn("portal login failed, user not exists, username={}", username);
                throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_USER_NOT_EXISTS);
            }
            userId = user.getUserId();
            if (!passwordEncoder.matches(password, user.getPassword())) {
                log.warn("portal login failed, password mismatch, username={}", username);
                throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_FAILED);
            }
            if (user.getStatus() == UserStatusEnums.DISABLE) {
                log.warn("portal login failed, user disabled, username={}", username);
                throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_FAILED);
            }

            SysUserEntity sysUser = toSysUser(user);
            LoginUser loginUser = new LoginUser(
                    sysUser.getUserId(), null, sysUser, permissionService.getMenuPermission(sysUser));
            AsyncManager.me().execute(AsyncFactory.recordLogin(
                    username, userId, userType, LoginRecordStatusEnums.SUCCESS,
                    "登录成功"));
            return loginUser;
        }
        catch (AbstractBusinessException e) {
            AsyncManager.me().execute(AsyncFactory.recordLogin(
                    username, userId, userType,
                    LoginRecordStatusEnums.FAIL, e.getEc().getMsg()));
            throw e;
        }
        catch (RuntimeException e) {
            String message = resolveExceptionMessage(e);
            log.warn("portal login authentication error, username={}", username, e);
            AsyncManager.me().execute(AsyncFactory.recordLogin(
                    username, userId, userType,
                    LoginRecordStatusEnums.FAIL, message));
            throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_FAILED, message);
        }
    }

    private SysUserEntity toSysUser(UserEntity user) {
        SysUserEntity sysUser = new SysUserEntity();
        sysUser.setUserId(user.getUserId());
        sysUser.setUserName(user.getUserName());
        sysUser.setNickName(user.getNickName());
        sysUser.setSupplierId(user.getSupplierId());
        sysUser.setSex(user.getSex());
        sysUser.setIdCardNumber(user.getIdCardNumber());
        sysUser.setTitle(user.getTitle());
        sysUser.setIdCardFront(user.getIdCardFront());
        sysUser.setIdCardBack(user.getIdCardBack());
        sysUser.setQualificationCertificate(user.getQualificationCertificate());
        sysUser.setUserType(user.getUserType());
        sysUser.setPhonenumber(user.getPhonenumber());
        sysUser.setPassword(user.getPassword());
        sysUser.setStatus(user.getStatus().getCode());
        sysUser.setReviewReason(user.getReviewReason());
        sysUser.setDelFlag(Boolean.TRUE.equals(user.getDelFlag()) ? "2" : "0");
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
