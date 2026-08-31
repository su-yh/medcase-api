package com.medcase.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.system.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class SysUserServiceImplUserTypeTest {

    private SysUserServiceImpl userService;

    @Mock
    private SysUserMapper userMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        userService = new SysUserServiceImpl();
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
    }

    @Test
    void checkUserNameUniqueDefaultsToAdminUserType() {

        when(userMapper.selectUserByUserNameAndType(any(), any(), any())).thenReturn(null);
        SysUser user = new SysUser();
        user.setUserName("same-name");

        userService.checkUserNameUnique(user);

        verify(userMapper).selectUserByUserNameAndType("same-name", UserTypeEnums.ADMIN, "0");
        assertEquals(UserTypeEnums.ADMIN, user.getUserType());
    }

    @Test
    void checkPhoneUniquePreservesExplicitDoctorUserType() {

        when(userMapper.selectUserByPhoneAndType(any(), any(), any())).thenReturn(null);
        SysUser user = new SysUser();
        user.setPhonenumber("15888888888");
        user.setUserType(UserTypeEnums.DOCTOR);

        userService.checkPhoneUnique(user);

        verify(userMapper).selectUserByPhoneAndType("15888888888", UserTypeEnums.DOCTOR, "0");
        assertEquals(UserTypeEnums.DOCTOR, user.getUserType());
    }

    @Test
    void checkEmailUniqueDefaultsToAdminUserType() {

        when(userMapper.selectUserByEmailAndType(any(), any(), any())).thenReturn(null);
        SysUser user = new SysUser();
        user.setEmail("same@example.com");

        userService.checkEmailUnique(user);

        verify(userMapper).selectUserByEmailAndType("same@example.com", UserTypeEnums.ADMIN, "0");
        assertEquals(UserTypeEnums.ADMIN, user.getUserType());
    }
}
