package com.medcase.biz.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.biz.request.UserLoginRequest;
import com.medcase.biz.request.UserRegisterRequest;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAuthServiceTest {
    private UserAuthService userAuthService;

    private Validator validator;

    @Mock
    private UserMapper userMapper;

    @Mock
    private com.medcase.framework.web.service.UserLoginService userLoginService;

    @Mock
    private com.medcase.framework.web.service.TokenService tokenService;

    @Mock
    private UserRegisterSmsCodeService smsCodeService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        userAuthService = new UserAuthService(
                userMapper, userLoginService, tokenService, smsCodeService, validator, passwordEncoder);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void registerInsertsUserWithEncryptedPassword() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000");
        when(userMapper.usernameExists("doctor01", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.phoneExists("13800000000", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.insert(any(UserEntity.class))).thenReturn(1);
        when(userMapper.updateById(any(UserEntity.class))).thenReturn(1);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");

        userAuthService.register(registerRequest);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userMapper).insert(captor.capture());
        UserEntity user = captor.getValue();
        assertEquals("doctor01", user.getUserName());
        assertEquals(null, user.getNickName());
        assertEquals(UserTypeEnums.DOCTOR, user.getUserType());
        assertEquals("13800000000", user.getPhonenumber());
        assertEquals(null, user.getSupplierId());
        assertEquals(UserStatusEnums.REGISTER, user.getStatus());
        assertEquals(null, user.getDelFlag());
        assertNotNull(user.getPwdUpdateDate());
        assertEquals("encoded-password", user.getPassword());
        verify(userMapper).usernameExists("doctor01", UserTypeEnums.DOCTOR);
        verify(smsCodeService).verifyCode("13800000000", "123456");
    }

    @Test
    void authServiceDoesNotDeclareValidationAnnotations() throws NoSuchMethodException {
        assertEquals(null, UserAuthService.class.getAnnotation(Validated.class));
        assertEquals(null, UserAuthService.class
                .getMethod("register", UserRegisterRequest.class)
                .getParameters()[0]
                .getAnnotation(Valid.class));
    }

    @Test
    void registerRejectsDuplicateUsername() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000");
        when(userMapper.usernameExists("doctor01", UserTypeEnums.DOCTOR)).thenReturn(true);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> userAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.USER_REGISTER_USER_EXISTS, exception.getEc());
        verify(userMapper, never()).insert(any(UserEntity.class));
        verify(userMapper).usernameExists("doctor01", UserTypeEnums.DOCTOR);
    }

    @Test
    void registerRejectsEmptyUsername() {
        UserRegisterRequest registerRequest = registerRequest(
                null, "secret123", "13800000000");

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("注册账号不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsEmptyPassword() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", null, "13800000000");

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("密码不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsInvalidUsernameLength() {
        UserRegisterRequest registerRequest = registerRequest(
                "d", "secret123", "13800000000");

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("注册账号长度必须在"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsInvalidPasswordLength() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "1234", "13800000000");

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("密码长度必须在"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsInsertFailure() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000");
        when(userMapper.usernameExists("doctor01", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.phoneExists("13800000000", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.insert(any(UserEntity.class))).thenReturn(0);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> userAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.USER_REGISTER_FAILED, exception.getEc());
        verify(userMapper).usernameExists("doctor01", UserTypeEnums.DOCTOR);
    }

    @Test
    void registerRejectsEmptyPhone() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", null);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("手机号不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerPatientDoesNotRequireDoctorQualification() {
        UserRegisterRequest registerRequest = registerRequest(
                "patient01", "secret123", "13800000000");
        registerRequest.setUserType(UserTypeEnums.PATIENT);
        when(userMapper.usernameExists("patient01", UserTypeEnums.PATIENT)).thenReturn(false);
        when(userMapper.phoneExists("13800000000", UserTypeEnums.PATIENT)).thenReturn(false);
        when(userMapper.insert(any(UserEntity.class))).thenReturn(1);

        userAuthService.register(registerRequest);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userMapper).insert(captor.capture());
        UserEntity user = captor.getValue();
        assertEquals(UserTypeEnums.PATIENT, user.getUserType());
        assertEquals(null, user.getTitle());
        assertEquals(null, user.getQualificationCertificate());
    }

    @Test
    void loginDelegatesToSharedUserLoginService() {
        UserLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        when(userLoginService.login(
                "doctor01", "secret123", null, null, UserTypeEnums.DOCTOR)).thenReturn("doctor-token");

        String response = userAuthService.login(loginRequest);

        assertEquals("doctor-token", response);
        verify(userLoginService).login(
                "doctor01", "secret123", null, null, UserTypeEnums.DOCTOR);
    }

    @Test
    void logoutDeletesCurrentLoginToken() {
        SysUser user = new SysUser();
        user.setUserId(12L);
        user.setUserName("doctor01");
        LoginUser loginUser = new LoginUser();
        loginUser.setToken("doctor-login-token");
        loginUser.setUserId(12L);
        loginUser.setUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null));

        userAuthService.logout(loginUser);

        verify(tokenService).delLoginUser("doctor-login-token");
    }

    @Test
    void deleteAccountAllowsDoctorBeforeApproval() {
        UserEntity doctor = user(
                "doctor01", "secret123", UserStatusEnums.REVIEW_FAILED);
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(userMapper.deleteById(12L)).thenReturn(1);

        LoginUser loginUser = doctorLoginUser(12L, "doctor-login-token");
        userAuthService.deleteAccount(loginUser);

        verify(userMapper).deleteById(12L);
        verify(tokenService).delLoginUser("doctor-login-token");
    }

    @Test
    void deleteAccountAllowsNormalDoctor() {
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR))
                .thenReturn(user("doctor01", "secret123", UserStatusEnums.OK));
        when(userMapper.deleteById(12L)).thenReturn(1);

        userAuthService.deleteAccount(doctorLoginUser(12L, "doctor-login-token"));

        verify(userMapper).deleteById(12L);
        verify(tokenService).delLoginUser("doctor-login-token");
    }

    @Test
    void deleteAccountRejectsDisabledDoctor() {
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR))
                .thenReturn(user("doctor01", "secret123", UserStatusEnums.DISABLE));

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> userAuthService.deleteAccount(doctorLoginUser(12L, "doctor-login-token")));

        assertEquals(ErrorCodeEnums.USER_ACCOUNT_DELETE_STATUS_NOT_MATCH, exception.getEc());
        verify(userMapper, never()).deleteById(12L);
    }

    private UserRegisterRequest registerRequest(
            String username, String password, String phone) {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);
        registerRequest.setUserType(UserTypeEnums.DOCTOR);
        registerRequest.setPhone(phone);
        registerRequest.setSmsCode("123456");
        return registerRequest;
    }

    private UserLoginRequest loginRequest(String username, String password) {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        return loginRequest;
    }

    private LoginUser doctorLoginUser(Long userId, String token) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setUserName("doctor01");
        user.setUserType(UserTypeEnums.DOCTOR);
        LoginUser loginUser = new LoginUser();
        loginUser.setToken(token);
        loginUser.setUserId(userId);
        loginUser.setUser(user);
        return loginUser;
    }

    private UserEntity user(String username, String rawPassword) {
        return user(username, rawPassword, UserStatusEnums.OK);
    }

    private UserEntity user(
            String username, String rawPassword, UserStatusEnums status) {
        UserEntity user = new UserEntity();
        user.setUserId(12L);
        user.setUserName(username);
        user.setNickName(username);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setPassword(rawPassword + "-hash");
        user.setStatus(status);
        return user;
    }
}
