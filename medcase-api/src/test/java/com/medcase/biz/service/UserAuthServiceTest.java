package com.medcase.biz.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.domain.SupplierEntity;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.biz.mapper.SupplierMapper;
import com.medcase.biz.request.UserLoginRequest;
import com.medcase.biz.request.UserRegisterRequest;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.storage.pojo.FileAttachment;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

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
    private SupplierMapper supplierMapper;

    @Mock
    private com.medcase.framework.web.service.SysLoginService loginService;

    @Mock
    private com.medcase.framework.web.service.SysPermissionService permissionService;

    @Mock
    private com.medcase.framework.web.service.TokenService tokenService;

    @Mock
    private UserRegisterSmsCodeService smsCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        when(supplierMapper.selectEnabledById(1L)).thenReturn(enabledSupplier());
        userAuthService = new UserAuthService(
                userMapper, supplierMapper, loginService, permissionService,
                tokenService, smsCodeService, validator);
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
                "doctor01", "secret123", "13800000000", 1L);
        when(userMapper.usernameExists("doctor01", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.phoneExists("13800000000", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.insert(any(UserEntity.class))).thenReturn(1);
        when(userMapper.updateById(any(UserEntity.class))).thenReturn(1);

        userAuthService.register(registerRequest);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userMapper).insert(captor.capture());
        UserEntity user = captor.getValue();
        assertEquals("doctor01", user.getUserName());
        assertEquals("张医生", user.getNickName());
        assertEquals(UserTypeEnums.DOCTOR, user.getUserType());
        assertEquals("13800000000", user.getPhonenumber());
        assertEquals(UserStatusEnums.REGISTER, user.getStatus());
        assertEquals(null, user.getDelFlag());
        assertNotNull(user.getPwdUpdateDate());
        assertTrue(SecurityUtils.matchesPassword("secret123", user.getPassword()));
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
                "doctor01", "secret123", "13800000000", 1L);
        when(userMapper.usernameExists("doctor01", UserTypeEnums.DOCTOR)).thenReturn(true);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> userAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.USER_REGISTER_USER_EXISTS, exception.getEc());
        verify(userMapper, never()).insert(any(UserEntity.class));
        verify(userMapper).usernameExists("doctor01", UserTypeEnums.DOCTOR);
    }

    @Test
    void registerRejectsEmptyUsername() {
        UserRegisterRequest registerRequest = registerRequest(
                null, "secret123", "13800000000", 1L);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("注册账号不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsEmptyPassword() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", null, "13800000000", 1L);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("密码不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsInvalidUsernameLength() {
        UserRegisterRequest registerRequest = registerRequest(
                "d", "secret123", "13800000000", 1L);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("注册账号长度必须在"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsInvalidPasswordLength() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "1234", "13800000000", 1L);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class, () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("密码长度必须在"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsInsertFailure() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", 1L);
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
                "doctor01", "secret123", null, 1L);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("手机号不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsEmptySex() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", 1L);
        registerRequest.setSex(null);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("性别不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsInvalidSupplier() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", 2L);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> userAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.USER_REGISTER_SUPPLIER_INVALID, exception.getEc());
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsDisabledSupplier() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", 1L);
        when(supplierMapper.selectEnabledById(1L)).thenReturn(null);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> userAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.USER_REGISTER_SUPPLIER_INVALID, exception.getEc());
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsEmptySupplierId() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", null);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("邀请人不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registrationUsesSupplierIdInsteadOfInviteCode() throws NoSuchFieldException {
        assertEquals(Long.class, UserRegisterRequest.class
                .getDeclaredField("supplierId").getType());
        assertThrows(NoSuchFieldException.class,
                () -> UserRegisterRequest.class.getDeclaredField("inviteCode"));
    }

    @Test
    void registerRejectsMissingRegistrationAttachment() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", 1L);
        registerRequest.setIdCardFront(null);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("身份证正面图片不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerRejectsDoctorWithoutQualificationByValidation() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", 1L);
        registerRequest.setQualificationCertificate(null);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> userAuthService.register(registerRequest));

        assertTrue(exception.getMessage().contains("医师职业资格证图片不能为空"));
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void registerPatientDoesNotRequireDoctorQualification() {
        UserRegisterRequest registerRequest = registerRequest(
                "patient01", "secret123", "13800000000", 1L);
        registerRequest.setUserType(UserTypeEnums.PATIENT);
        registerRequest.setTitle(null);
        registerRequest.setQualificationCertificate(null);
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
    void registerRejectsDuplicateDoctorPhone() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", 1L);
        when(userMapper.usernameExists("doctor01", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.phoneExists("13800000000", UserTypeEnums.DOCTOR)).thenReturn(true);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> userAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.USER_REGISTER_PHONE_EXISTS, exception.getEc());
        verify(userMapper, never()).insert(any(UserEntity.class));
    }

    @Test
    void loginCreatesTokenForUserAndUpdatesLoginInfo() {
        UserLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        UserEntity doctor = user("doctor01", "secret123");
        doctor.setDelFlag(Boolean.FALSE);
        when(userMapper.selectUserByUsername("doctor01", UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of("doctor:home"));
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        String response = userAuthService.login(loginRequest);

        ArgumentCaptor<LoginUser> loginUserCaptor = ArgumentCaptor.forClass(LoginUser.class);
        verify(tokenService).createToken(loginUserCaptor.capture());
        LoginUser loginUser = loginUserCaptor.getValue();
        assertEquals("doctor-token", response);
        assertEquals(12L, loginUser.getUserId());
        assertEquals(UserTypeEnums.DOCTOR, loginUser.getUser().getUserType());
        assertEquals(Set.of("doctor:home"), loginUser.getPermissions());
        ArgumentCaptor<SysUser> sysUserCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(permissionService).getMenuPermission(sysUserCaptor.capture());
        assertEquals(Boolean.FALSE, sysUserCaptor.getValue().getDelFlag());
        verify(userMapper).selectUserByUsername("doctor01", UserTypeEnums.DOCTOR);
        verify(userMapper).updateById(any(UserEntity.class));
    }

    @Test
    void loginCopiesDeletedDoctorDelFlagToSysUser() {
        UserLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        UserEntity doctor = user("doctor01", "secret123");
        doctor.setDelFlag(Boolean.TRUE);
        when(userMapper.selectUserByUsername("doctor01", UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of());
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        userAuthService.login(loginRequest);

        ArgumentCaptor<SysUser> sysUserCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(permissionService).getMenuPermission(sysUserCaptor.capture());
        assertEquals(Boolean.TRUE, sysUserCaptor.getValue().getDelFlag());
    }

    @Test
    void registerStoresUserProfileAndRegistrationAttachments() {
        UserRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", 1L);
        registerRequest.setNickName("张医生");
        registerRequest.setSex("1");
        registerRequest.setIdCardNumber("110101199001011234");
        registerRequest.setTitle("主治医师");
        when(userMapper.usernameExists("doctor01", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.phoneExists("13800000000", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.insert(any(UserEntity.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, UserEntity.class).setUserId(12L);
            return 1;
        });
        userAuthService.register(registerRequest);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userMapper).insert(captor.capture());
        UserEntity user = captor.getValue();
        assertEquals("张医生", user.getNickName());
        assertEquals("1", user.getSex());
        assertEquals("110101199001011234", user.getIdCardNumber());
        assertEquals("主治医师", user.getTitle());
        assertEquals("id-card-front.png", user.getIdCardFront().getOriginalFilename());
        assertEquals("id-card-back.png", user.getIdCardBack().getOriginalFilename());
        assertEquals("qualification.png",
                user.getQualificationCertificate().getOriginalFilename());
    }

    @Test
    void loginRejectsMissingUser() {
        UserLoginRequest loginRequest = loginRequest("sameName", "secret123");
        when(userMapper.selectUserByUsername("sameName", UserTypeEnums.DOCTOR)).thenReturn(null);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> userAuthService.login(loginRequest));

        assertEquals(ErrorCodeEnums.USER_LOGIN_USER_NOT_EXISTS, exception.getEc());
        verify(userMapper).selectUserByUsername("sameName", UserTypeEnums.DOCTOR);
        verify(tokenService, never()).createToken(any(LoginUser.class));
    }

    @Test
    void loginCreatesTokenForPendingReviewDoctor() {
        UserLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        when(userMapper.selectUserByUsername("doctor01", UserTypeEnums.DOCTOR))
                .thenReturn(user("doctor01", "secret123", UserStatusEnums.PENDING_REVIEW));
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of());
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        assertEquals("doctor-token", userAuthService.login(loginRequest));
        verify(tokenService).createToken(any(LoginUser.class));
    }

    @Test
    void loginCreatesTokenForRegisteredDoctor() {
        UserLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        UserEntity doctor = user(
                "doctor01", "secret123", UserStatusEnums.REGISTER);
        when(userMapper.selectUserByUsername("doctor01", UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of());
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        assertEquals("doctor-token", userAuthService.login(loginRequest));

        verify(tokenService).createToken(any(LoginUser.class));
    }

    @Test
    void loginRejectsDisabledDoctor() {
        UserLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        when(userMapper.selectUserByUsername("doctor01", UserTypeEnums.DOCTOR))
                .thenReturn(user("doctor01", "secret123", UserStatusEnums.DISABLE));

        AbstractBusinessException exception =
                assertThrows(AbstractBusinessException.class, () -> userAuthService.login(loginRequest));

        assertEquals(ErrorCodeEnums.USER_LOGIN_FAILED, exception.getEc());
        verify(tokenService, never()).createToken(any(LoginUser.class));
    }

    @Test
    void loginCreatesTokenForDoctorWhoseReviewFailed() {
        UserLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        when(userMapper.selectUserByUsername("doctor01", UserTypeEnums.DOCTOR))
                .thenReturn(user("doctor01", "secret123", UserStatusEnums.REVIEW_FAILED));
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of());
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        assertEquals("doctor-token", userAuthService.login(loginRequest));

        verify(tokenService).createToken(any(LoginUser.class));
    }
    @Test
    void loginRejectsWrongPassword() {
        UserLoginRequest loginRequest = loginRequest("doctor01", "badpass");
        when(userMapper.selectUserByUsername("doctor01", UserTypeEnums.DOCTOR)).thenReturn(user("doctor01", "secret123"));

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> userAuthService.login(loginRequest));

        assertEquals(ErrorCodeEnums.USER_LOGIN_FAILED, exception.getEc());
        verify(userMapper).selectUserByUsername("doctor01", UserTypeEnums.DOCTOR);
        verify(tokenService, never()).createToken(any(LoginUser.class));
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
            String username, String password, String phone, Long supplierId) {
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);
        registerRequest.setUserType(UserTypeEnums.DOCTOR);
        registerRequest.setPhone(phone);
        registerRequest.setSupplierId(supplierId);
        registerRequest.setSmsCode("123456");
        registerRequest.setNickName("张医生");
        registerRequest.setSex("1");
        registerRequest.setIdCardNumber("110101199001011234");
        registerRequest.setTitle("主治医师");
        registerRequest.setIdCardFront(attachment("id-card-front.png"));
        registerRequest.setIdCardBack(attachment("id-card-back.png"));
        registerRequest.setQualificationCertificate(attachment("qualification.png"));
        return registerRequest;
    }

    private SupplierEntity enabledSupplier() {
        SupplierEntity supplier = new SupplierEntity();
        supplier.setId(1L);
        supplier.setNickName("供应商A");
        supplier.setStatus("0");
        return supplier;
    }

    private FileAttachment attachment(String filename) {
        FileAttachment attachment = new FileAttachment();
        attachment.setOriginalFilename(filename);
        attachment.setFilePath("doctor/" + filename);
        return attachment;
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
        user.setPassword(SecurityUtils.encryptPassword(rawPassword));
        user.setStatus(status);
        return user;
    }
}
