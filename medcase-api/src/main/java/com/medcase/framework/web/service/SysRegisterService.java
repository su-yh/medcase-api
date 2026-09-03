package com.medcase.framework.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.medcase.common.constant.CacheConstants;
import com.medcase.common.constant.Constants;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.RegisterBody;
import com.medcase.common.core.redis.RedisCache;
import com.medcase.common.exception.user.CaptchaException;
import com.medcase.common.exception.user.CaptchaExpireException;
import com.medcase.common.utils.DateUtils;
import com.medcase.common.utils.MessageUtils;
import com.medcase.common.utils.StringUtils;
import com.medcase.framework.manager.AsyncManager;
import com.medcase.framework.manager.factory.AsyncFactory;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.service.ISysConfigService;
import com.medcase.system.service.ISysUserService;

/**
 * 注册校验方法
 * 
 */
@Component
public class SysRegisterService {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 注册
     */
    public void register(RegisterBody registerBody) {

        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled) {

            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_REGISTER_USERNAME_EMPTY);
        }
        else if (StringUtils.isEmpty(password)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_REGISTER_PASSWORD_EMPTY);
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_REGISTER_USERNAME_LENGTH_INVALID);
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_REGISTER_PASSWORD_LENGTH_INVALID);
        }
        else if (!userService.checkUserNameUnique(sysUser)) {
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_REGISTER_USER_EXISTS, username);
        }
        else {

            sysUser.setNickName(username);
            sysUser.setPwdUpdateDate(DateUtils.getNowDate());
            sysUser.setPassword(passwordEncoder.encode(password));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag) {
                throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_REGISTER_FAILED);
            }
            else {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
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

        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null) {

            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {

            throw new CaptchaException();
        }
    }
}
