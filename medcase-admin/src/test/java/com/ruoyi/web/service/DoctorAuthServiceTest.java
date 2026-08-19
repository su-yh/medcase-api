package com.ruoyi.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.AbstractBusinessException;
import com.ruoyi.system.domain.DoctorUserEntity;
import com.ruoyi.system.mapper.DoctorUserMapper;
import java.util.Date;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
        doctorAuthService = new DoctorAuthService(doctorUserMapper, loginService, permissionService, tokenService);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void registerInsertsDoctorUserWithEncryptedPassword() {
        RegisterBody registerBody = registerBody("doctor01", "secret123");
        when(doctorUserMapper.usernameExists("doctor01")).thenReturn(false);
        when(doctorUserMapper.insert(any(DoctorUserEntity.class))).thenReturn(1);

        doctorAuthService.register(registerBody);

        ArgumentCaptor<DoctorUserEntity> captor = ArgumentCaptor.forClass(DoctorUserEntity.class);
        verify(doctorUserMapper).insert(captor.capture());
        DoctorUserEntity user = captor.getValue();
        assertEquals("doctor01", user.getUserName());
        assertEquals("doctor01", user.getNickName());
        assertEquals(UserTypeEnums.DOCTOR, user.getUserType());
        assertEquals(UserStatus.OK.getCode(), user.getStatus());
        assertEquals(null, user.getDelFlag());
        assertNotNull(user.getPwdUpdateDate());
        assertTrue(SecurityUtils.matchesPassword("secret123", user.getPassword()));
        verify(loginService).validateCaptcha("doctor01", null, null);
        verify(doctorUserMapper).usernameExists("doctor01");
    }

    @Test
    void registerRejectsDuplicateDoctorUsername() {
        RegisterBody registerBody = registerBody("doctor01", "secret123");
        when(doctorUserMapper.usernameExists("doctor01")).thenReturn(true);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerBody));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_USER_EXISTS, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
        verify(doctorUserMapper).usernameExists("doctor01");
    }

    @Test
    void registerRejectsEmptyUsername() {
        RegisterBody registerBody = registerBody("", "secret123");

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerBody));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_USERNAME_EMPTY, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsEmptyPassword() {
        RegisterBody registerBody = registerBody("doctor01", "");

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerBody));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_PASSWORD_EMPTY, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsInvalidUsernameLength() {
        RegisterBody registerBody = registerBody("d", "secret123");

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerBody));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_USERNAME_LENGTH_INVALID, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsInvalidPasswordLength() {
        RegisterBody registerBody = registerBody("doctor01", "1234");

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerBody));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_PASSWORD_LENGTH_INVALID, exception.getEc());
        verify(doctorUserMapper, never()).insert(any(DoctorUserEntity.class));
    }

    @Test
    void registerRejectsInsertFailure() {
        RegisterBody registerBody = registerBody("doctor01", "secret123");
        when(doctorUserMapper.usernameExists("doctor01")).thenReturn(false);
        when(doctorUserMapper.insert(any(DoctorUserEntity.class))).thenReturn(0);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.register(registerBody));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_FAILED, exception.getEc());
        verify(doctorUserMapper).usernameExists("doctor01");
    }

    @Test
    void loginCreatesTokenForDoctorUserAndUpdatesLoginInfo() {
        LoginBody loginBody = loginBody("doctor01", "secret123");
        DoctorUserEntity doctor = doctorUser("doctor01", "secret123");
        when(doctorUserMapper.selectDoctorByUsername("doctor01")).thenReturn(doctor);
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of("doctor:home"));
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        String response = doctorAuthService.login(loginBody);

        ArgumentCaptor<LoginUser> loginUserCaptor = ArgumentCaptor.forClass(LoginUser.class);
        verify(tokenService).createToken(loginUserCaptor.capture());
        LoginUser loginUser = loginUserCaptor.getValue();
        assertEquals("doctor-token", response);
        assertEquals(12L, loginUser.getUserId());
        assertEquals(UserTypeEnums.DOCTOR, loginUser.getUser().getUserType());
        assertEquals(Set.of("doctor:home"), loginUser.getPermissions());
        verify(doctorUserMapper).selectDoctorByUsername("doctor01");
        verify(doctorUserMapper).updateById(any(DoctorUserEntity.class));
    }

    @Test
    void loginRejectsMissingDoctorUser() {
        LoginBody loginBody = loginBody("sameName", "secret123");
        when(doctorUserMapper.selectDoctorByUsername("sameName")).thenReturn(null);

        AbstractBusinessException exception = assertThrows(AbstractBusinessException.class, () -> doctorAuthService.login(loginBody));

        assertEquals(ErrorCodeEnums.DOCTOR_LOGIN_USER_NOT_EXISTS, exception.getEc());
        verify(doctorUserMapper).selectDoctorByUsername("sameName");
        verify(tokenService, never()).createToken(any(LoginUser.class));
    }

    @Test
    void loginRejectsWrongPassword() {
        LoginBody loginBody = loginBody("doctor01", "badpass");
        when(doctorUserMapper.selectDoctorByUsername("doctor01")).thenReturn(doctorUser("doctor01", "secret123"));

        assertThrows(UserPasswordNotMatchException.class, () -> doctorAuthService.login(loginBody));

        verify(doctorUserMapper).selectDoctorByUsername("doctor01");
        verify(tokenService, never()).createToken(any(LoginUser.class));
    }

    private RegisterBody registerBody(String username, String password) {
        RegisterBody registerBody = new RegisterBody();
        registerBody.setUsername(username);
        registerBody.setPassword(password);
        return registerBody;
    }

    private LoginBody loginBody(String username, String password) {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername(username);
        loginBody.setPassword(password);
        return loginBody;
    }

    private DoctorUserEntity doctorUser(String username, String rawPassword) {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(12L);
        user.setDeptId(3L);
        user.setUserName(username);
        user.setNickName(username);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setPassword(SecurityUtils.encryptPassword(rawPassword));
        user.setStatus(UserStatus.OK.getCode());
        return user;
    }
}
