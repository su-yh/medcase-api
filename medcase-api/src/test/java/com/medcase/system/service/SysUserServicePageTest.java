package com.medcase.system.service;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.mapper.SysUserMapper;
import com.medcase.system.entity.SysUserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysUserServicePageTest {
    private SysUserService userService;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysDeptService deptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new SysUserService();
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
        ReflectionTestUtils.setField(userService, "deptService", deptService);
    }

    @Test
    void selectPageDefaultsToAdminUserTypeAndReturnsPageResult() {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(2);
        pageParam.setPageSize(10);
        SysUser user = new SysUser();
        user.setUserName("admin");
        user.setDeptId(1L);

        SysUser resultUser = new SysUser();
        resultUser.setUserId(1L);
        resultUser.setUserName("admin");
        resultUser.setUserType(UserTypeEnums.ADMIN);
        resultUser.setDeptId(2L);

        SysDept resultDept = new SysDept();
        resultDept.setDeptId(2L);
        resultDept.setDeptName("子部门");
        when(deptService.all()).thenReturn(List.of());
        when(deptService.selectDeptById(2L)).thenReturn(resultDept);
        when(userMapper.selectPage(
                any(PageParam.class), any(SysUser.class), any(Collection.class),
                nullable(String.class), nullable(String.class)))
                .thenReturn(new PageResult<>(List.of(resultUser), 1L));

        PageResult<SysUser> result = userService.selectPage(user, pageParam);

        ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
        ArgumentCaptor<Collection> deptIdsCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(userMapper).selectPage(
                any(PageParam.class), userCaptor.capture(), deptIdsCaptor.capture(),
                nullable(String.class), nullable(String.class));
        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getList().get(0).getUserId());
        assertEquals(UserTypeEnums.ADMIN, userCaptor.getValue().getUserType());
        assertEquals(List.of(1L), deptIdsCaptor.getValue());
        assertEquals("子部门", result.getList().get(0).getDept().getDeptName());
    }

    @Test
    void selectUserByIdFillsDeptFromCache() {

        SysUserEntity entity = new SysUserEntity();
        entity.setUserId(7L);
        entity.setDeptId(2L);
        when(userMapper.selectById(7L)).thenReturn(entity);

        SysDept dept = new SysDept();
        dept.setDeptId(2L);
        dept.setDeptName("子部门");
        when(deptService.selectDeptById(2L)).thenReturn(dept);

        SysUser result = userService.selectUserById(7L);

        assertEquals("子部门", result.getDept().getDeptName());
    }

    @Test
    void selectUserByUserNameFillsDeptFromCache() {

        SysUserEntity entity = new SysUserEntity();
        entity.setUserId(7L);
        entity.setUserName("admin");
        entity.setDeptId(2L);
        when(userMapper.selectUserByUserName("admin", UserTypeEnums.ADMIN.getCode(), "0"))
                .thenReturn(entity);

        SysDept dept = new SysDept();
        dept.setDeptId(2L);
        dept.setDeptName("子部门");
        when(deptService.selectDeptById(2L)).thenReturn(dept);

        SysUser result = userService.selectUserByUserName("admin", UserTypeEnums.ADMIN.getCode());

        assertEquals("子部门", result.getDept().getDeptName());
    }
}
