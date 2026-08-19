package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.system.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class SysUserServiceImplUserTypeTest
{
    private SysUserServiceImpl userService;

    @Mock
    private SysUserMapper userMapper;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        userService = new SysUserServiceImpl();
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
    }

    @Test
    void checkUserNameUniqueDefaultsToAdminUserType()
    {
        when(userMapper.checkUserNameUnique(any(SysUser.class))).thenReturn(null);
        SysUser user = new SysUser();
        user.setUserName("same-name");

        userService.checkUserNameUnique(user);

        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).checkUserNameUnique(captor.capture());
        assertEquals(UserTypeEnums.ADMIN, captor.getValue().getUserType());
    }

    @Test
    void checkPhoneUniquePreservesExplicitDoctorUserType()
    {
        when(userMapper.checkPhoneUnique(any(SysUser.class))).thenReturn(null);
        SysUser user = new SysUser();
        user.setPhonenumber("15888888888");
        user.setUserType(UserTypeEnums.DOCTOR);

        userService.checkPhoneUnique(user);

        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).checkPhoneUnique(captor.capture());
        assertEquals(UserTypeEnums.DOCTOR, captor.getValue().getUserType());
    }

    @Test
    void checkEmailUniqueDefaultsToAdminUserType()
    {
        when(userMapper.checkEmailUnique(any(SysUser.class))).thenReturn(null);
        SysUser user = new SysUser();
        user.setEmail("same@example.com");

        userService.checkEmailUnique(user);

        ArgumentCaptor<SysUser> captor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).checkEmailUnique(captor.capture());
        assertEquals(UserTypeEnums.ADMIN, captor.getValue().getUserType());
    }
}
