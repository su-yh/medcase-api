package com.medcase.framework.web.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.core.redis.RedisCache;
import com.medcase.system.service.ISysConfigService;
import com.medcase.system.service.ISysUserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
    private ISysUserService userService;

    @Mock
    private ISysConfigService configService;

    @Mock
    private SysPasswordService passwordService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SysPermissionService permissionService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        service = new UserLoginService(
                tokenService,
                redisCache,
                userService,
                configService,
                passwordService,
                userDetailsService,
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

        UserEntity user = new UserEntity();
        user.setUserId(12L);
        user.setUserName("doctor01");
        user.setPassword("encoded-password");
        user.setStatus(UserStatusEnums.OK);
        user.setUserType(UserTypeEnums.DOCTOR);
        when(userMapper.selectUserByUsername("doctor01", UserTypeEnums.DOCTOR)).thenReturn(user);
        when(passwordEncoder.matches("secret123", "encoded-password")).thenReturn(true);
        when(permissionService.getMenuPermission(any(SysUser.class))).thenReturn(Set.of("case:read"));
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("doctor-token");

        String token = service.login("doctor01", "secret123", null, null, UserTypeEnums.DOCTOR);

        assertEquals("doctor-token", token);
        ArgumentCaptor<LoginUser> loginUserCaptor = ArgumentCaptor.forClass(LoginUser.class);
        verify(tokenService).createToken(loginUserCaptor.capture());
        assertEquals(UserTypeEnums.DOCTOR, loginUserCaptor.getValue().getUser().getUserType());
        verify(userMapper).updateById(any(UserEntity.class));
    }
}
