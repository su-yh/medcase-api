package com.ruoyi.biz.service;

import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.biz.mapper.DoctorUserMapper;
import com.ruoyi.biz.request.DoctorLoginRequest;
import com.ruoyi.biz.request.DoctorRegisterRequest;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatusEnums;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import com.ruoyi.storage.pojo.FileAttachment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
    private static final String REGISTER_INVITE_CODE = "9999";

    private final DoctorUserMapper doctorUserMapper;

    private final SysLoginService loginService;

    private final SysPermissionService permissionService;

    private final TokenService tokenService;

    @Transactional(rollbackFor = Exception.class)
    public void register(DoctorRegisterRequest registerBody) {
        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String phone = registerBody.getPhone();
        log.info("doctor register request, username={}", username);

        if (!StringUtils.hasText(username)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_USERNAME_EMPTY);
        } else if (!StringUtils.hasText(password)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_PASSWORD_EMPTY);
        } else if (!StringUtils.hasText(phone)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_PHONE_EMPTY);
        } else if (!StringUtils.hasText(registerBody.getNickName())) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_NICKNAME_EMPTY);
        } else if (!StringUtils.hasText(registerBody.getIdCardNumber())) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_ID_CARD_NUMBER_EMPTY);
        } else if (!StringUtils.hasText(registerBody.getTitle())) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_TITLE_EMPTY);
        } else if (!StringUtils.hasText(registerBody.getInviteCode())) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_INVITE_CODE_EMPTY);
        } else if (!REGISTER_INVITE_CODE.equals(registerBody.getInviteCode())) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_INVITE_CODE_INVALID);
        } else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_USERNAME_LENGTH_INVALID);
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_PASSWORD_LENGTH_INVALID);
        }
        requireUploadFile(registerBody.getIdCardFront(),
                ErrorCodeEnums.DOCTOR_REGISTER_ID_CARD_FRONT_EMPTY);
        requireUploadFile(registerBody.getIdCardBack(),
                ErrorCodeEnums.DOCTOR_REGISTER_ID_CARD_BACK_EMPTY);
        requireUploadFile(registerBody.getQualificationCertificate(),
                ErrorCodeEnums.DOCTOR_REGISTER_QUALIFICATION_CERTIFICATE_EMPTY);

        if (doctorUserMapper.usernameExists(username)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_USER_EXISTS, username);
        }
        if (doctorUserMapper.phoneExists(phone)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_PHONE_EXISTS);
        }
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserName(username);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setPhonenumber(phone);

        user.setNickName(registerBody.getNickName().trim());
        user.setIdCardNumber(registerBody.getIdCardNumber().trim());
        user.setTitle(registerBody.getTitle().trim());
        user.setIdCardFront(registerBody.getIdCardFront());
        user.setIdCardBack(registerBody.getIdCardBack());
        user.setQualificationCertificate(registerBody.getQualificationCertificate());
        user.setStatus(UserStatusEnums.REGISTER);
        user.setPwdUpdateDate(DateUtils.getNowDate());
        user.setPassword(SecurityUtils.encryptPassword(password));
        if (doctorUserMapper.insert(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_FAILED);
        }
        log.info("doctor register success, username={}", username);
    }

    private void requireUploadFile(FileAttachment file, ErrorCodeEnums errorCode) {
        if (file == null || !StringUtils.hasText(file.getFilePath())) {
            throw ExceptionUtil.business(errorCode);
        }
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
        } else if (!SecurityUtils.matchesPassword(loginBody.getPassword(), doctorUser.getPassword())) {
            log.warn("doctor login failed, password mismatch, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_LOGIN_FAILED);
        }
        if (doctorUser.getStatus() == UserStatusEnums.DISABLE) {
            log.warn("doctor login failed, user disabled, username={}",
                    username);
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

    public void logout(LoginUser doctorUser) {
        tokenService.delLoginUser(doctorUser.getToken());
        log.info("doctor logout success, username={}, userId={}", doctorUser.getUsername(), doctorUser.getUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(LoginUser doctorUser) {
        DoctorUserEntity currentDoctor = doctorUserMapper.selectDoctorById(doctorUser.getUserId());
        if (currentDoctor == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_NOT_FOUND);
        }

        UserStatusEnums status = currentDoctor.getStatus();
        if (status == UserStatusEnums.DISABLE) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_ACCOUNT_DELETE_STATUS_NOT_MATCH);
        }

        if (doctorUserMapper.deleteById(currentDoctor.getUserId()) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_ACCOUNT_DELETE_FAILED);
        }

        tokenService.delLoginUser(doctorUser.getToken());
        log.info("doctor account deleted, username={}, userId={}",
                currentDoctor.getUserName(), currentDoctor.getUserId());
    }

    private SysUser toSysUser(DoctorUserEntity doctorUser) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(doctorUser.getUserId());
        sysUser.setUserName(doctorUser.getUserName());
        sysUser.setNickName(doctorUser.getNickName());
        sysUser.setUserType(doctorUser.getUserType());
        sysUser.setPassword(doctorUser.getPassword());
        sysUser.setStatus(doctorUser.getStatus().getCode());
        sysUser.setDelFlag(doctorUser.getDelFlag());
        sysUser.setLoginIp(doctorUser.getLoginIp());
        sysUser.setLoginDate(doctorUser.getLoginDate());
        sysUser.setPwdUpdateDate(doctorUser.getPwdUpdateDate());
        return sysUser;
    }
}
