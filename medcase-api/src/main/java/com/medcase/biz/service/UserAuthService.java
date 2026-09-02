package com.medcase.biz.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.biz.request.UserLoginRequest;
import com.medcase.biz.request.UserRegisterRequest;
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
 * 病例端用户注册登录校验方法
 *
 * @author suyh
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserAuthService {
    private static final String REGISTER_INVITE_CODE = "9999";

    private final UserMapper userMapper;

    private final SysLoginService loginService;

    private final SysPermissionService permissionService;

    private final TokenService tokenService;

    private final UserRegisterSmsCodeService smsCodeService;

    private final Validator validator;

    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterRequest registerBody) {
        validateRegisterRequest(registerBody);

        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String phone = registerBody.getPhone();
        UserTypeEnums userType = registerBody.getUserType();
        log.info("user register request, username={}", username);

        if (!REGISTER_INVITE_CODE.equals(registerBody.getInviteCode())) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_REGISTER_INVITE_CODE_INVALID);
        }

        if (userMapper.usernameExists(username, userType)) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_REGISTER_USER_EXISTS, username);
        }
        if (userMapper.phoneExists(phone, userType)) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_REGISTER_PHONE_EXISTS);
        }
        smsCodeService.verifyCode(phone, registerBody.getSmsCode());

        UserEntity user = new UserEntity();
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
        if (userMapper.insert(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_REGISTER_FAILED);
        }
        log.info("user register success, username={}", username);
    }

    private void validateRegisterRequest(UserRegisterRequest request) {
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
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

    public String login(UserLoginRequest loginBody) {
        String username = loginBody.getUsername();
        UserTypeEnums userType = resolveUserType(loginBody.getUserType());
        log.info("user login request, username={}", username);
        loginService.validateCaptcha(username, loginBody.getCode(), loginBody.getUuid());
        loginService.loginPreCheck(username, loginBody.getPassword());

        UserEntity user = userMapper.selectUserByUsername(username, userType);
        if (user == null) {
            log.warn("user login failed, user not exists, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_USER_NOT_EXISTS);
        } else if (!SecurityUtils.matchesPassword(loginBody.getPassword(), user.getPassword())) {
            log.warn("user login failed, password mismatch, username={}", username);
            throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_FAILED);
        }
        if (user.getStatus() == UserStatusEnums.DISABLE) {
            log.warn("user login failed, user disabled, username={}",
                    username);
            throw ExceptionUtil.business(ErrorCodeEnums.USER_LOGIN_FAILED);
        }

        UserEntity updateUser = new UserEntity();
        updateUser.setUserId(user.getUserId());
        updateUser.setLoginIp(IpUtils.getIpAddr());
        updateUser.setLoginDate(DateUtils.getNowDate());
        userMapper.updateById(updateUser);
        SysUser sysUser = toSysUser(user);
        LoginUser loginUser = new LoginUser(
                sysUser.getUserId(), null, sysUser,
                permissionService.getMenuPermission(sysUser));
        log.info("user login success, username={}, userId={}", username, user.getUserId());
        return tokenService.createToken(loginUser);
    }

    public void logout(LoginUser user) {
        tokenService.delLoginUser(user.getToken());
        log.info("user logout success, username={}, userId={}", user.getUsername(), user.getUserId());
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(LoginUser user) {
        UserTypeEnums userType = user.getUser() == null ? null : user.getUser().getUserType();
        UserEntity currentUser = userMapper.selectUserById(user.getUserId(), userType);
        if (currentUser == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_NOT_FOUND);
        }

        UserStatusEnums status = currentUser.getStatus();
        if (status == UserStatusEnums.DISABLE) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_ACCOUNT_DELETE_STATUS_NOT_MATCH);
        }

        if (userMapper.deleteById(currentUser.getUserId()) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_ACCOUNT_DELETE_FAILED);
        }

        tokenService.delLoginUser(user.getToken());
        log.info("user account deleted, username={}, userId={}",
                currentUser.getUserName(), currentUser.getUserId());
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

    private UserTypeEnums resolveUserType(String userTypeCode) {
        UserTypeEnums userType = UserTypeEnums.fromCode(userTypeCode);
        return userType == null ? UserTypeEnums.DOCTOR : userType;
    }
}
