package com.medcase.biz.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.mapper.SupplierMapper;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.biz.request.UserLoginRequest;
import com.medcase.biz.request.UserRegisterRequest;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.DateUtils;
import com.medcase.framework.web.service.TokenService;
import com.medcase.framework.web.service.UserLoginService;
import com.medcase.common.validation.groups.ValidationGroups;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UserMapper userMapper;

    private final UserLoginService userLoginService;

    private final TokenService tokenService;

    private final UserRegisterSmsCodeService smsCodeService;

    private final Validator validator;

    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackFor = Exception.class)
    public void register(UserRegisterRequest registerBody) {
        validateRegisterRequest(registerBody);

        String username = registerBody.getUsername();
        String password = registerBody.getPassword();
        String phone = registerBody.getPhone();
        UserTypeEnums userType = registerBody.getUserType();
        log.info("user register request, username={}", username);

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
        user.setReviewReason(null);
        user.setStatus(UserStatusEnums.REGISTER);
        user.setPwdUpdateDate(DateUtils.getNowDate());
        user.setPassword(passwordEncoder.encode(password));
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
    }

    public String login(UserLoginRequest loginBody) {
        UserTypeEnums userType = resolveUserType(loginBody.getUserType());
        return userLoginService.login(
                loginBody.getUsername(),
                loginBody.getPassword(),
                loginBody.getCode(),
                loginBody.getUuid(),
                userType);
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

    private UserTypeEnums resolveUserType(String userTypeCode) {
        UserTypeEnums userType = UserTypeEnums.fromCode(userTypeCode);
        return userType == null ? UserTypeEnums.DOCTOR : userType;
    }
}
