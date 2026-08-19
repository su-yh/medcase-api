package com.ruoyi.framework.web.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.core.domain.model.DoctorLoginResponse;
import com.ruoyi.common.enums.UserStatus;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.exception.user.UserNotExistsException;
import com.ruoyi.common.exception.user.UserPasswordNotMatchException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.mapper.DoctorUserMapper;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class DoctorAuthServiceTest {
    private DoctorAuthService doctorAuthService;

    @Mock
    private DoctorUserMapper doctorUserMapper;

    @Mock
    private SysLoginService loginService;

    @Mock
    private SysPermissionService permissionService;

    @Mock
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorAuthService = new DoctorAuthService();
        ReflectionTestUtils.setField(doctorAuthService, "doctorUserMapper", doctorUserMapper);
        ReflectionTestUtils.setField(doctorAuthService, "loginService", loginService);
        ReflectionTestUtils.setField(doctorAuthService, "permissionService", permissionService);
        ReflectionTestUtils.setField(doctorAuthService, "tokenService", tokenService);
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
        when(doctorUserMapper.selectCount(any(Wrapper.class))).thenReturn(0L);
        when(doctorUserMapper.insert(any(SysUser.class))).thenReturn(1);

        doctorAuthService.register(registerBody);

        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(doctorUserMapper).insert(captor.capture());
        SysUser user = captor.getValue();
        assertEquals("doctor01", user.getUserName());
        assertEquals("doctor01", user.getNickName());
        assertEquals(UserTypeEnums.DOCTOR, user.getUserType());
        assertEquals(UserStatus.OK.getCode(), user.getStatus());
        assertEquals("0", user.getDelFlag());
        assertNotNull(user.getPwdUpdateDate());
        assertTrue(SecurityUtils.matchesPassword("secret123", user.getPassword()));
        verify(loginService).validateCaptcha("doctor01", null, null);
    }

    @Test
    void registerRejectsDuplicateDoctorUsername() {
        RegisterBody registerBody = registerBody("doctor01", "secret123");
        when(doctorUserMapper.selectCount(any(Wrapper.class))).thenReturn(1L);

        ServiceException exception = assertThrows(ServiceException.class, () -> doctorAuthService.register(registerBody));

        assertEquals("保存用户'doctor01'失败，注册账号已存在", exception.getMessage());
        verify(doctorUserMapper, never()).insert(any(SysUser.class));
    }

    @Test
    void loginCreatesTokenForDoctorUserAndUpdatesLoginInfo() {
        LoginBody loginBody = loginBody("doctor01", "secret123");
        SysUser doctor = doctorUser("doctor01", "secret123");
        when(doctorUserMapper.selectOne(any(Wrapper.class))).thenReturn(doctor);
        when(permissionService.getMenuPermission(doctor)).thenReturn(Set.of("doctor:home"));
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        DoctorLoginResponse response = doctorAuthService.login(loginBody);

        ArgumentCaptor<LoginUser> loginUserCaptor = ArgumentCaptor.forClass(LoginUser.class);
        verify(tokenService).createToken(loginUserCaptor.capture());
        LoginUser loginUser = loginUserCaptor.getValue();
        assertEquals("doctor-token", response.getToken());
        assertEquals(12L, loginUser.getUserId());
        assertEquals(UserTypeEnums.DOCTOR, loginUser.getUser().getUserType());
        assertEquals(Set.of("doctor:home"), loginUser.getPermissions());
        verify(doctorUserMapper).update(eq(null), any(Wrapper.class));
    }

    @Test
    void loginOnlyQueriesDoctorUsers() {
        LoginBody loginBody = loginBody("sameName", "secret123");
        when(doctorUserMapper.selectOne(any(Wrapper.class))).thenReturn(null);

        assertThrows(UserNotExistsException.class, () -> doctorAuthService.login(loginBody));

        ArgumentCaptor<Wrapper<SysUser>> captor = ArgumentCaptor.forClass(Wrapper.class);
        verify(doctorUserMapper).selectOne(captor.capture());
        String sqlSegment = captor.getValue().getSqlSegment();
        Map<String, Object> params = ((AbstractWrapper<SysUser, ?, ?>) captor.getValue()).getParamNameValuePairs();
        assertTrue(sqlSegment.contains("user_name"));
        assertTrue(sqlSegment.contains("user_type"));
        assertTrue(params.containsValue("sameName"));
        assertTrue(params.containsValue(UserTypeEnums.DOCTOR) || params.containsValue(UserTypeEnums.DOCTOR.getCode()));
        verify(tokenService, never()).createToken(any(LoginUser.class));
    }

    @Test
    void loginRejectsWrongPassword() {
        LoginBody loginBody = loginBody("doctor01", "badpass");
        when(doctorUserMapper.selectOne(any(Wrapper.class))).thenReturn(doctorUser("doctor01", "secret123"));

        assertThrows(UserPasswordNotMatchException.class, () -> doctorAuthService.login(loginBody));

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

    private SysUser doctorUser(String username, String rawPassword) {
        SysUser user = new SysUser();
        user.setUserId(12L);
        user.setDeptId(3L);
        user.setUserName(username);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setPassword(SecurityUtils.encryptPassword(rawPassword));
        user.setStatus(UserStatus.OK.getCode());
        user.setDelFlag("0");
        return user;
    }
}
