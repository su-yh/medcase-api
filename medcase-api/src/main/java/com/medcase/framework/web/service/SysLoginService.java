package com.medcase.framework.web.service;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.stereotype.Component;
import com.medcase.common.constant.CacheConstants;
import com.medcase.common.constant.Constants;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.core.redis.RedisCache;
import com.medcase.common.utils.DateUtils;
import com.medcase.common.utils.MessageUtils;
import com.medcase.common.utils.StringUtils;
import com.medcase.common.utils.ip.IpUtils;
import com.medcase.framework.manager.AsyncManager;
import com.medcase.framework.manager.factory.AsyncFactory;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.service.ISysConfigService;
import com.medcase.system.service.ISysUserService;

/**
 * 登录校验方法
 * 
 */
@Component
@Slf4j
public class SysLoginService {

    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationConfiguration authenticationConfiguration;

    @Autowired
    private RedisCache redisCache;
    
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private SysPasswordService passwordService;

    /**
     * 登录验证
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String login(String username, String password, String code, String uuid) {

        // 验证码校验
        validateCaptcha(username, code, uuid);
        // 登录前置校验
        loginPreCheck(username, password);
        // 登录失败次数校验
        passwordService.validateLoginRetryCount(username);
        // 用户验证
        Authentication authentication = null;
        try {

            AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager.authenticate(authenticationToken);
        }
        catch (AuthenticationException e) {
            log.warn("AuthenticationException, username: {}", username, e);

            if (e instanceof BadCredentialsException) {

                passwordService.recordLoginFailure(username);
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
            }
            String message = resolveExceptionMessage(e);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, message));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_AUTHENTICATION_ERROR, message);
        }
        catch (Exception e) {

            String message = resolveExceptionMessage(e);
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, message));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_AUTHENTICATION_ERROR, message);
        }
        passwordService.clearLoginRecordCache(username);
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid) {

        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {

            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            if (captcha == null) {

                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_EXPIRED);
            }
            redisCache.deleteObject(verifyKey);
            if (!code.equalsIgnoreCase(captcha)) {

                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_CAPTCHA_INVALID);
            }
        }
    }

    /**
     * 登录前置校验
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password) {

        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {

            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_PARAMETER_EMPTY);
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {

            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {

            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr())) {

            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_IP_BLOCKED);
        }
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId) {

        userService.updateLoginInfo(userId, IpUtils.getIpAddr(), DateUtils.getNowDate());
    }

    private String resolveExceptionMessage(Throwable throwable) {

        if (StringUtils.isNotEmpty(throwable.getMessage())) {

            return throwable.getMessage();
        }
        return throwable.getClass().getSimpleName();
    }
}
