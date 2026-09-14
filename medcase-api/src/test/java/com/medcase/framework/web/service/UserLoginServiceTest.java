package com.medcase.framework.web.service;

import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.mapper.SysUserMapper;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.core.redis.RedisCache;
import com.medcase.common.utils.spring.SpringUtils;
import com.medcase.system.service.LoginRecordService;
import com.medcase.system.service.SysConfigService;
import com.medcase.system.service.SysUserService;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserLoginServiceTest {

    private UserLoginService service;

    @Mock
    private TokenService tokenService;

    @Mock
    private RedisCache redisCache;

    @Mock
    private SysConfigService configService;

    @Mock
    private SysPasswordService passwordService;

    @Mock
    private SysUserService userService;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysPermissionService permissionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        beanFactory.registerSingleton("scheduledExecutorService", mock(ScheduledExecutorService.class));
        beanFactory.registerSingleton("loginRecordService", mock(LoginRecordService.class));
        ReflectionTestUtils.setField(SpringUtils.class, "beanFactory", beanFactory);
        service = new UserLoginService(
                tokenService,
                redisCache,
                configService,
                passwordService,
                userService,
                userMapper,
                permissionService,
                passwordEncoder);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void loginCreatesTokenForDoctor() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(configService.selectConfigByKey("sys.login.blackIPList")).thenReturn(null);

        SysUserEntity user = new SysUserEntity();
        user.setUserId(12L);
        user.setUserName("doctor01");
        user.setPassword("encoded-password");
        user.setStatus(UserStatusEnums.OK);
        user.setUserType(UserTypeEnums.DOCTOR);
        when(userMapper.selectUserByUserName("doctor01", UserTypeEnums.DOCTOR)).thenReturn(user);
        when(passwordEncoder.matches("secret123", "encoded-password")).thenReturn(true);
        when(permissionService.getMenuPermission(any(SysUserEntity.class))).thenReturn(Set.of("case:read"));
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        String token = service.login("doctor01", "secret123", null, null, UserTypeEnums.DOCTOR);

        assertEquals("doctor-token", token);
        ArgumentCaptor<LoginUser> loginUserCaptor = ArgumentCaptor.forClass(LoginUser.class);
        verify(tokenService).createToken(loginUserCaptor.capture());
        assertEquals(UserTypeEnums.DOCTOR, loginUserCaptor.getValue().getUser().getUserType());
    }

    @Test
    void loginCreatesTokenForAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(configService.selectConfigByKey("sys.login.blackIPList")).thenReturn(null);

        SysUserEntity user = new SysUserEntity();
        user.setUserId(1L);
        user.setUserName("admin");
        user.setPassword("encoded-password");
        user.setStatus(UserStatusEnums.OK);
        user.setUserType(UserTypeEnums.ADMIN);
        when(userService.selectUserByUserName("admin", UserTypeEnums.ADMIN)).thenReturn(user);
        when(passwordEncoder.matches("secret123", "encoded-password")).thenReturn(true);
        when(permissionService.getMenuPermission(user)).thenReturn(Set.of("system:user:list"));
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("admin-token");

        String token = service.login("admin", "secret123", null, null, UserTypeEnums.ADMIN);

        assertEquals("admin-token", token);
        verify(userService).selectUserByUserName("admin", UserTypeEnums.ADMIN);
    }

    @Test
    void loginRejectsDisabledAdmin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(configService.selectCaptchaEnabled()).thenReturn(false);
        when(configService.selectConfigByKey("sys.login.blackIPList")).thenReturn(null);

        SysUserEntity user = new SysUserEntity();
        user.setUserId(1L);
        user.setUserName("admin");
        user.setStatus(UserStatusEnums.DISABLE);
        user.setUserType(UserTypeEnums.ADMIN);
        when(userService.selectUserByUserName("admin", UserTypeEnums.ADMIN)).thenReturn(user);

        AbstractBusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
                AbstractBusinessException.class,
                () -> service.login("admin", "secret123", null, null, UserTypeEnums.ADMIN));

        assertEquals(ErrorCodeEnums.USER_BLOCKED, exception.getEc());
    }
}
