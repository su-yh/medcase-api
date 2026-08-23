package com.ruoyi.biz.service;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.biz.request.DoctorLoginRequest;
import com.ruoyi.biz.request.DoctorRegisterRequest;
import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.biz.mapper.DoctorUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 医生端注册登录校验方法
 *
 * @author suyh
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DoctorAuthService {
    private final DoctorUserMapper doctorUserMapper;

    private final SysLoginService loginService;

    private final SysPermissionService permissionService;

    private final TokenService tokenService;

    public void register(DoctorRegisterRequest registerBody) {
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        log.info("doctor register request, username={}", username);

        if (!StringUtils.hasText(username)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_USERNAME_EMPTY);
        } else if (!StringUtils.hasText(password)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_PASSWORD_EMPTY);
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_USERNAME_LENGTH_INVALID);
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_PASSWORD_LENGTH_INVALID);
        } else if (existsDoctorUsername(username)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_USER_EXISTS, username);
        }

        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserName(username);
        user.setNickName(username);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatus.OK.getCode());
        user.setPwdUpdateDate(DateUtils.getNowDate());
        user.setPassword(SecurityUtils.encryptPassword(password));
        if (doctorUserMapper.insert(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_FAILED);
        }
        log.info("doctor register success, username={}", username);
    }

    public String login(DoctorLoginRequest loginBody) {
        String username = loginBody.getUsername();
        log.info("doctor login request, username={}", username);
        loginService.validateCaptcha(username, loginBody.getCode(), loginBody.getUuid());
        loginService.loginPreCheck(username, loginBody.getPassword());

        DoctorUserEntity doctorUser = doctorUserMapper.selectDoctorByUsername(username);
        if (doctorUser == null) {
            log.warn("doctor login failed, user not exists, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_LOGIN_USER_NOT_EXISTS);
        } else if (UserStatus.DISABLE.getCode().equals(doctorUser.getStatus())) {
            log.warn("doctor login failed, user disabled, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_LOGIN_FAILED);
        } else if (!SecurityUtils.matchesPassword(loginBody.getPassword(), doctorUser.getPassword())) {
            log.warn("doctor login failed, password mismatch, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_LOGIN_FAILED);
        }

        DoctorUserEntity updateDoctorUser = new DoctorUserEntity();
        updateDoctorUser.setUserId(doctorUser.getUserId());
        updateDoctorUser.setLoginIp(IpUtils.getIpAddr());
        updateDoctorUser.setLoginDate(DateUtils.getNowDate());
        doctorUserMapper.updateById(updateDoctorUser);
        SysUser sysUser = toSysUser(doctorUser);
        LoginUser loginUser = new LoginUser(
                sysUser.getUserId(), null, sysUser,
                permissionService.getMenuPermission(sysUser));
        log.info("doctor login success, username={}, userId={}", username, doctorUser.getUserId());
        return tokenService.createToken(loginUser);
    }

    public void logout() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        tokenService.delLoginUser(loginUser.getToken());
        log.info("doctor logout success, username={}, userId={}", loginUser.getUsername(), loginUser.getUserId());
    }

    private boolean existsDoctorUsername(String username) {
        return doctorUserMapper.usernameExists(username);
    }

    private SysUser toSysUser(DoctorUserEntity doctorUser) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(doctorUser.getUserId());
        sysUser.setUserName(doctorUser.getUserName());
        sysUser.setNickName(doctorUser.getNickName());
        sysUser.setUserType(doctorUser.getUserType());
        sysUser.setPassword(doctorUser.getPassword());
        sysUser.setStatus(doctorUser.getStatus());
        sysUser.setDelFlag(doctorUser.getDelFlag());
        sysUser.setLoginIp(doctorUser.getLoginIp());
        sysUser.setLoginDate(doctorUser.getLoginDate());
        sysUser.setPwdUpdateDate(doctorUser.getPwdUpdateDate());
        return sysUser;
    }
}
