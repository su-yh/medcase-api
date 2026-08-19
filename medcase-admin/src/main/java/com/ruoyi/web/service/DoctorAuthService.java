package com.ruoyi.web.service;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.domain.DoctorUserEntity;
import com.ruoyi.system.mapper.DoctorUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    public void register(RegisterBody registerBody) {
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        log.info("doctor register request, username={}", username);
        loginService.validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());

        if (StringUtils.isEmpty(username)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_USERNAME_EMPTY);
        } else if (StringUtils.isEmpty(password)) {
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

    public String login(LoginBody loginBody) {
        String username = loginBody.getUsername();
        log.info("doctor login request, username={}", username);
        loginService.validateCaptcha(username, loginBody.getCode(), loginBody.getUuid());
        loginService.loginPreCheck(username, loginBody.getPassword());

        DoctorUserEntity doctorUser = doctorUserMapper.selectDoctorByUsername(username);
        if (doctorUser == null) {
            log.warn("doctor login failed, user not exists, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_LOGIN_USER_NOT_EXISTS);
        } else if (UserStatus.DISABLE.getCode().equals(doctorUser.getStatus())) {
            throw new UserPasswordNotMatchException();
        } else if (!SecurityUtils.matchesPassword(loginBody.getPassword(), doctorUser.getPassword())) {
            throw new UserPasswordNotMatchException();
        }

        DoctorUserEntity updateDoctorUser = new DoctorUserEntity();
        updateDoctorUser.setUserId(doctorUser.getUserId());
        updateDoctorUser.setLoginIp(IpUtils.getIpAddr());
        updateDoctorUser.setLoginDate(DateUtils.getNowDate());
        doctorUserMapper.updateById(updateDoctorUser);
        SysUser sysUser = toSysUser(doctorUser);
        LoginUser loginUser = new LoginUser(sysUser.getUserId(), sysUser.getDeptId(), sysUser,
                permissionService.getMenuPermission(sysUser));
        log.info("doctor login success, username={}, userId={}", username, doctorUser.getUserId());
        return tokenService.createToken(loginUser);
    }

    private boolean existsDoctorUsername(String username) {
        return doctorUserMapper.usernameExists(username);
    }

    private SysUser toSysUser(DoctorUserEntity doctorUser) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(doctorUser.getUserId());
        sysUser.setDeptId(doctorUser.getDeptId());
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
