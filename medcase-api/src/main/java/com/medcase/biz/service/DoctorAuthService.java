package com.medcase.biz.service;

import com.medcase.biz.domain.DoctorUserEntity;
import com.medcase.biz.mapper.DoctorUserMapper;
import com.medcase.biz.request.DoctorLoginRequest;
import com.medcase.biz.request.DoctorRegisterRequest;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.DateUtils;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.common.utils.ip.IpUtils;
import com.medcase.framework.web.service.SysLoginService;
import com.medcase.framework.web.service.SysPermissionService;
import com.medcase.framework.web.service.TokenService;
import com.medcase.common.validation.groups.ValidationGroups;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 病例端注册登录校验方法
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

    private final DoctorRegisterSmsCodeService smsCodeService;

    private final Validator validator;

    @Transactional(rollbackFor = Exception.class)
    public void register(DoctorRegisterRequest registerBody) {
        validateRegisterRequest(registerBody);

        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String phone = registerBody.getPhone();
        UserTypeEnums userType = registerBody.getUserType();
        log.info("doctor register request, username={}", username);

        if (!REGISTER_INVITE_CODE.equals(registerBody.getInviteCode())) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_INVITE_CODE_INVALID);
        }

        if (doctorUserMapper.usernameExists(username, userType)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_USER_EXISTS, username);
        }
        if (doctorUserMapper.phoneExists(phone, userType)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_PHONE_EXISTS);
        }
        smsCodeService.verifyCode(phone, registerBody.getSmsCode());

        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserName(username);
        user.setUserType(userType);
        user.setPhonenumber(phone);

        user.setNickName(registerBody.getNickName().trim());
        user.setSex(registerBody.getSex().trim());
        user.setIdCardNumber(registerBody.getIdCardNumber().trim());
        if (userType == UserTypeEnums.DOCTOR) {
            user.setTitle(registerBody.getTitle().trim());
            user.setQualificationCertificate(registerBody.getQualificationCertificate());
        }
        user.setIdCardFront(registerBody.getIdCardFront());
        user.setIdCardBack(registerBody.getIdCardBack());
        user.setReviewReason(null);
        user.setStatus(UserStatusEnums.REGISTER);
        user.setPwdUpdateDate(DateUtils.getNowDate());
        user.setPassword(SecurityUtils.encryptPassword(password));
        if (doctorUserMapper.insert(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_FAILED);
        }
        log.info("doctor register success, username={}", username);
    }

    private void validateRegisterRequest(DoctorRegisterRequest request) {
        Set<ConstraintViolation<DoctorRegisterRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        UserTypeEnums userType = request.getUserType();
        if (userType != UserTypeEnums.DOCTOR && userType != UserTypeEnums.PATIENT) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_TYPE_NOT_MATCH);
        }
        if (userType == UserTypeEnums.DOCTOR) {
            violations = validator.validate(request, ValidationGroups.Doctor.Submit.class);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }
    }

    public String login(DoctorLoginRequest loginBody) {
        String username = loginBody.getUsername();
        UserTypeEnums userType = resolveUserType(loginBody.getUserType());
        log.info("doctor login request, username={}", username);
        loginService.validateCaptcha(username, loginBody.getCode(), loginBody.getUuid());
        loginService.loginPreCheck(username, loginBody.getPassword());

        DoctorUserEntity doctorUser = doctorUserMapper.selectUserByUsername(username, userType);
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
        UserTypeEnums userType = doctorUser.getUser() == null ? null : doctorUser.getUser().getUserType();
        DoctorUserEntity currentDoctor = doctorUserMapper.selectUserById(doctorUser.getUserId(), userType);
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
        sysUser.setSex(doctorUser.getSex());
        sysUser.setUserType(doctorUser.getUserType());
        sysUser.setPassword(doctorUser.getPassword());
        sysUser.setStatus(doctorUser.getStatus().getCode());
        sysUser.setDelFlag(doctorUser.getDelFlag());
        sysUser.setLoginIp(doctorUser.getLoginIp());
        sysUser.setLoginDate(doctorUser.getLoginDate());
        sysUser.setPwdUpdateDate(doctorUser.getPwdUpdateDate());
        return sysUser;
    }

    private UserTypeEnums resolveUserType(String userTypeCode) {
        UserTypeEnums userType = UserTypeEnums.fromCode(userTypeCode);
        return userType == null ? UserTypeEnums.DOCTOR : userType;
    }
}
