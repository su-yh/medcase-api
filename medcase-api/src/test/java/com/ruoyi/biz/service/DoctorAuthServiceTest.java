package com.ruoyi.biz.service;

import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.biz.mapper.DoctorUserMapper;
import com.ruoyi.biz.request.DoctorLoginRequest;
import com.ruoyi.biz.request.DoctorRegisterRequest;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatusEnums;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.AbstractBusinessException;
import com.ruoyi.storage.pojo.FileAttachment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

class DoctorAuthServiceTest {
    private DoctorAuthService doctorAuthService;

    @Mock
    private DoctorUserMapper doctorUserMapper;

    @Mock
    private com.ruoyi.framework.web.service.SysLoginService loginService;

    @Mock
    private com.ruoyi.framework.web.service.SysPermissionService permissionService;

    @Mock
    private com.ruoyi.framework.web.service.TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorAuthService = new DoctorAuthService(
                doctorUserMapper, loginService, permissionService, tokenService);
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
    void registerInsertsDoctorUserWithEncryptedPassword() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", "9999");
        when(doctorUserMapper.usernameExists("doctor01")).thenReturn(false);
        when(doctorUserMapper.phoneExists("13800000000")).thenReturn(false);
        when(doctorUserMapper.insert(any(DoctorUserEntity.class))).thenReturn(1);
        when(doctorUserMapper.updateById(any(DoctorUserEntity.class))).thenReturn(1);

        doctorAuthService.register(registerRequest);

        ArgumentCaptor<DoctorUserEntity> captor = ArgumentCaptor.forClass(DoctorUserEntity.class);
        verify(doctorUserMapper).insert(captor.capture());
        DoctorUserEntity user = captor.getValue();
        assertEquals("doctor01", user.getUserName());
        assertEquals("张医生", user.getNickName());
        assertEquals(UserTypeEnums.DOCTOR, user.getUserType());
        assertEquals("13800000000", user.getPhonenumber());
        assertEquals(UserStatusEnums.REGISTER, user.getStatus());
        assertEquals(null, user.getDelFlag());
        assertNotNull(user.getPwdUpdateDate());
        assertTrue(SecurityUtils.matchesPassword("secret123", user.getPassword()));
        verify(doctorUserMapper).usernameExists("doctor01");
    }

    @Test
    void registerRejectsDuplicateDoctorUsername() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", "9999");
        when(doctorUserMapper.usernameExists("doctor01")).thenReturn(true);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_USER_EXISTS, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
        verify(doctorUserMapper).usernameExists("doctor01");
    }

    @Test
    void registerRejectsEmptyUsername() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "", "secret123", "13800000000", "9999");

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_USERNAME_EMPTY, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsEmptyPassword() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "", "13800000000", "9999");

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_PASSWORD_EMPTY, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsInvalidUsernameLength() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "d", "secret123", "13800000000", "9999");

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_USERNAME_LENGTH_INVALID, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsInvalidPasswordLength() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "1234", "13800000000", "9999");

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_PASSWORD_LENGTH_INVALID, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsInsertFailure() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", "9999");
        when(doctorUserMapper.usernameExists("doctor01")).thenReturn(false);
        when(doctorUserMapper.phoneExists("13800000000")).thenReturn(false);
        when(doctorUserMapper.insert(any(DoctorUserEntity.class))).thenReturn(0);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_FAILED, exception.getEc());
        verify(doctorUserMapper).usernameExists("doctor01");
    }

    @Test
    void registerRejectsEmptyPhone() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "", "9999");

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_PHONE_EMPTY, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsEmptySex() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", "9999");
        registerRequest.setSex("");

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_SEX_EMPTY, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsInvalidInviteCode() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", "1234");

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_INVITE_CODE_INVALID, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsEmptyInviteCode() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", "");

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_INVITE_CODE_EMPTY, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsMissingRegistrationAttachment() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", "9999");
        registerRequest.setIdCardFront(null);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_ID_CARD_FRONT_EMPTY, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsDuplicateDoctorPhone() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", "9999");
        when(doctorUserMapper.usernameExists("doctor01")).thenReturn(false);
        when(doctorUserMapper.phoneExists("13800000000")).thenReturn(true);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorAuthService.register(registerRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_PHONE_EXISTS, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void loginCreatesTokenForDoctorUserAndUpdatesLoginInfo() {
        DoctorLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        DoctorUserEntity doctor = doctorUser("doctor01", "secret123");
        doctor.setDelFlag(Boolean.FALSE);
        when(doctorUserMapper.selectDoctorByUsername("doctor01")).thenReturn(doctor);
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of("doctor:home"));
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        String response = doctorAuthService.login(loginRequest);

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
        verify(doctorUserMapper).selectDoctorByUsername("doctor01");
        verify(doctorUserMapper).updateById(any(DoctorUserEntity.class));
    }

    @Test
    void loginCopiesDeletedDoctorDelFlagToSysUser() {
        DoctorLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        DoctorUserEntity doctor = doctorUser("doctor01", "secret123");
        doctor.setDelFlag(Boolean.TRUE);
        when(doctorUserMapper.selectDoctorByUsername("doctor01")).thenReturn(doctor);
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of());
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        doctorAuthService.login(loginRequest);

        ArgumentCaptor<SysUser> sysUserCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(permissionService).getMenuPermission(sysUserCaptor.capture());
        assertEquals(Boolean.TRUE, sysUserCaptor.getValue().getDelFlag());
    }

    @Test
    void registerStoresDoctorProfileAndRegistrationAttachments() {
        DoctorRegisterRequest registerRequest = registerRequest(
                "doctor01", "secret123", "13800000000", "9999");
        registerRequest.setNickName("张医生");
        registerRequest.setSex("1");
        registerRequest.setIdCardNumber("110101199001011234");
        registerRequest.setTitle("主治医师");
        when(doctorUserMapper.usernameExists("doctor01")).thenReturn(false);
        when(doctorUserMapper.phoneExists("13800000000")).thenReturn(false);
        when(doctorUserMapper.insert(any(DoctorUserEntity.class))).thenAnswer(invocation -> {
            invocation.getArgument(0, DoctorUserEntity.class).setUserId(12L);
            return 1;
        });
        doctorAuthService.register(registerRequest);

        ArgumentCaptor<DoctorUserEntity> captor = ArgumentCaptor.forClass(DoctorUserEntity.class);
        verify(doctorUserMapper).insert(captor.capture());
        DoctorUserEntity user = captor.getValue();
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
    void loginRejectsMissingDoctorUser() {
        DoctorLoginRequest loginRequest = loginRequest("sameName", "secret123");
        when(doctorUserMapper.selectDoctorByUsername("sameName")).thenReturn(null);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.login(loginRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_LOGIN_USER_NOT_EXISTS, exception.getEc());
        verify(doctorUserMapper).selectDoctorByUsername("sameName");
        verify(tokenService, never()).createToken(any(LoginUser.class));
    }

    @Test
    void loginCreatesTokenForPendingReviewDoctor() {
        DoctorLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        when(doctorUserMapper.selectDoctorByUsername("doctor01"))
                .thenReturn(doctorUser("doctor01", "secret123", UserStatusEnums.PENDING_REVIEW));
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of());
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        assertEquals("doctor-token", doctorAuthService.login(loginRequest));
        verify(tokenService).createToken(any(LoginUser.class));
    }

    @Test
    void loginCreatesTokenForRegisteredDoctor() {
        DoctorLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        DoctorUserEntity doctor = doctorUser(
                "doctor01", "secret123", UserStatusEnums.REGISTER);
        when(doctorUserMapper.selectDoctorByUsername("doctor01")).thenReturn(doctor);
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of());
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        assertEquals("doctor-token", doctorAuthService.login(loginRequest));

        verify(tokenService).createToken(any(LoginUser.class));
    }

    @Test
    void loginRejectsDisabledDoctor() {
        DoctorLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        when(doctorUserMapper.selectDoctorByUsername("doctor01"))
                .thenReturn(doctorUser("doctor01", "secret123", UserStatusEnums.DISABLE));

        AbstractBusinessException exception =
                assertThrows(AbstractBusinessException.class, () -> doctorAuthService.login(loginRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_LOGIN_FAILED, exception.getEc());
        verify(tokenService, never()).createToken(any(LoginUser.class));
    }

    @Test
    void loginCreatesTokenForDoctorWhoseReviewFailed() {
        DoctorLoginRequest loginRequest = loginRequest("doctor01", "secret123");
        when(doctorUserMapper.selectDoctorByUsername("doctor01"))
                .thenReturn(doctorUser("doctor01", "secret123", UserStatusEnums.REVIEW_FAILED));
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of());
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        assertEquals("doctor-token", doctorAuthService.login(loginRequest));

        verify(tokenService).createToken(any(LoginUser.class));
    }
    @Test
    void loginRejectsWrongPassword() {
        DoctorLoginRequest loginRequest = loginRequest("doctor01", "badpass");
        when(doctorUserMapper.selectDoctorByUsername("doctor01")).thenReturn(doctorUser("doctor01", "secret123"));

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.login(loginRequest));

        assertEquals(ErrorCodeEnums.DOCTOR_LOGIN_FAILED, exception.getEc());
        verify(doctorUserMapper).selectDoctorByUsername("doctor01");
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

        doctorAuthService.logout(loginUser);

        verify(tokenService).delLoginUser("doctor-login-token");
    }

    @Test
    void deleteAccountAllowsDoctorBeforeApproval() {
        DoctorUserEntity doctor = doctorUser(
                "doctor01", "secret123", UserStatusEnums.REVIEW_FAILED);
        when(doctorUserMapper.selectDoctorById(12L)).thenReturn(doctor);
        when(doctorUserMapper.deleteById(12L)).thenReturn(1);

        LoginUser loginUser = doctorLoginUser(12L, "doctor-login-token");
        doctorAuthService.deleteAccount(loginUser);

        verify(doctorUserMapper).deleteById(12L);
        verify(tokenService).delLoginUser("doctor-login-token");
    }

    @Test
    void deleteAccountAllowsNormalDoctor() {
        when(doctorUserMapper.selectDoctorById(12L))
                .thenReturn(doctorUser("doctor01", "secret123", UserStatusEnums.OK));
        when(doctorUserMapper.deleteById(12L)).thenReturn(1);

        doctorAuthService.deleteAccount(doctorLoginUser(12L, "doctor-login-token"));

        verify(doctorUserMapper).deleteById(12L);
        verify(tokenService).delLoginUser("doctor-login-token");
    }

    @Test
    void deleteAccountRejectsDisabledDoctor() {
        when(doctorUserMapper.selectDoctorById(12L))
                .thenReturn(doctorUser("doctor01", "secret123", UserStatusEnums.DISABLE));

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorAuthService.deleteAccount(doctorLoginUser(12L, "doctor-login-token")));

        assertEquals(ErrorCodeEnums.DOCTOR_ACCOUNT_DELETE_STATUS_NOT_MATCH, exception.getEc());
        verify(doctorUserMapper, never()).deleteById(12L);
    }

    private DoctorRegisterRequest registerRequest(
            String username, String password, String phone, String inviteCode) {
        DoctorRegisterRequest registerRequest = new DoctorRegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);
        registerRequest.setPhone(phone);
        registerRequest.setInviteCode(inviteCode);
        registerRequest.setNickName("张医生");
        registerRequest.setSex("1");
        registerRequest.setIdCardNumber("110101199001011234");
        registerRequest.setTitle("主治医师");
        registerRequest.setIdCardFront(attachment("id-card-front.png"));
        registerRequest.setIdCardBack(attachment("id-card-back.png"));
        registerRequest.setQualificationCertificate(attachment("qualification.png"));
        return registerRequest;
    }

    private FileAttachment attachment(String filename) {
        FileAttachment attachment = new FileAttachment();
        attachment.setOriginalFilename(filename);
        attachment.setFilePath("doctor/" + filename);
        return attachment;
    }

    private DoctorLoginRequest loginRequest(String username, String password) {
        DoctorLoginRequest loginRequest = new DoctorLoginRequest();
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

    private DoctorUserEntity doctorUser(String username, String rawPassword) {
        return doctorUser(username, rawPassword, UserStatusEnums.OK);
    }

    private DoctorUserEntity doctorUser(
            String username, String rawPassword, UserStatusEnums status) {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(12L);
        user.setUserName(username);
        user.setNickName(username);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setPassword(SecurityUtils.encryptPassword(rawPassword));
        user.setStatus(status);
        return user;
    }
}
