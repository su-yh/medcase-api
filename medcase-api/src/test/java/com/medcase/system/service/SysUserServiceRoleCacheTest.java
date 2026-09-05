package com.medcase.system.service;

import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.mapper.SysUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysUserServiceRoleCacheTest {

    private SysUserService userService;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SysRoleService roleService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        userService = new SysUserService();
        ReflectionTestUtils.setField(userService, "userMapper", userMapper);
        ReflectionTestUtils.setField(userService, "roleService", roleService);
    }

    @Test
    void selectUserRoleGroupUsesCachedRolesForCurrentUser() {

        SysRoleEntity selectedRole = role(1L, "管理员", true);
        SysRoleEntity unselectedRole = role(2L, "普通用户", false);
        when(roleService.selectRolesByUserId(100L))
                .thenReturn(List.of(selectedRole, unselectedRole));

        String roleGroup = userService.selectUserRoleGroup(100L);

        assertEquals("管理员", roleGroup);
        verify(roleService).selectRolesByUserId(100L);
    }

    private SysRoleEntity role(Long roleId, String roleName, boolean flag) {

        SysRoleEntity role = new SysRoleEntity();
        role.setRoleId(roleId);
        role.setRoleName(roleName);
        role.setFlag(flag);
        return role;
    }
}
