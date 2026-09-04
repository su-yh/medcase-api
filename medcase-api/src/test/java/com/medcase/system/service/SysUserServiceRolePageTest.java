package com.medcase.system.service;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysUserServiceRolePageTest {
    private SysUserService userService;

    @Mock
    private SysUserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new SysUserService();
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
    }

    @Test
    void selectAllocatedPageDefaultsToAdminUserTypeAndReturnsPageResult() {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(1);
        pageParam.setPageSize(10);
        SysUser user = new SysUser();
        user.setRoleId(99L);

        SysUser resultUser = new SysUser();
        resultUser.setUserId(7L);
        resultUser.setUserName("bound-user");
        resultUser.setUserType(UserTypeEnums.ADMIN);

        when(userMapper.selectAllocatedPage(any(PageParam.class), any(SysUser.class)))
                .thenReturn(new PageResult<>(List.of(resultUser), 1L));

        PageResult<SysUser> result = userService.selectAllocatedPage(user, pageParam);

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).selectAllocatedPage(any(PageParam.class), userCaptor.capture());
        assertEquals(UserTypeEnums.ADMIN, userCaptor.getValue().getUserType());
        assertEquals(1, result.getTotal());
        assertEquals(7L, result.getList().get(0).getUserId());
    }

    @Test
    void selectUnallocatedPageDefaultsToAdminUserTypeAndReturnsPageResult() {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(1);
        pageParam.setPageSize(10);
        SysUser user = new SysUser();
        user.setRoleId(99L);

        SysUser resultUser = new SysUser();
        resultUser.setUserId(8L);
        resultUser.setUserName("free-user");
        resultUser.setUserType(UserTypeEnums.ADMIN);

        when(userMapper.selectUnallocatedPage(any(PageParam.class), any(SysUser.class)))
                .thenReturn(new PageResult<>(List.of(resultUser), 1L));

        PageResult<SysUser> result = userService.selectUnallocatedPage(user, pageParam);

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        verify(userMapper).selectUnallocatedPage(any(PageParam.class), userCaptor.capture());
        assertEquals(UserTypeEnums.ADMIN, userCaptor.getValue().getUserType());
        assertEquals(1, result.getTotal());
        assertEquals(8L, result.getList().get(0).getUserId());
    }
}
