package com.medcase.system.service;

import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.mapper.SysMenuMapper;
import com.medcase.system.mapper.SysRoleMenuMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysMenuServiceRoleCacheTest {

    private SysMenuService menuService;

    @Mock
    private SysMenuMapper menuMapper;

    @Mock
    private SysRoleService roleService;

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        menuService = new SysMenuService();
        ReflectionTestUtils.setField(menuService, "menuMapper", menuMapper);
        ReflectionTestUtils.setField(menuService, "roleService", roleService);
        ReflectionTestUtils.setField(menuService, "roleMenuMapper", roleMenuMapper);
    }

    @Test
    void selectMenuListByRoleIdUsesCachedRole() {

        SysRoleEntity role = new SysRoleEntity();
        role.setRoleId(1L);
        role.setMenuCheckStrictly(Boolean.TRUE);
        when(roleService.selectRoleById(1L)).thenReturn(role);
        when(menuMapper.selectMenuListByRoleId(1L, true)).thenReturn(List.of(10L));

        List<Long> menuIds = menuService.selectMenuListByRoleId(1L);

        assertEquals(List.of(10L), menuIds);
        verify(roleService).selectRoleById(1L);
        verify(menuMapper).selectMenuListByRoleId(1L, true);
    }
}
