package com.medcase.system.service;

import com.medcase.common.core.domain.entity.SysMenu;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.mapper.SysRoleMapper;
import com.medcase.system.mapper.SysRoleMenuMapper;
import com.medcase.system.mapper.SysUserRoleMapper;
import com.medcase.web.controller.system.dto.RoleMenuUpdateRequest;
import com.medcase.web.controller.system.dto.RoleQueryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysRoleServiceOwnershipTest {

    private SysRoleService roleService;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @Mock
    private SysMenuService menuService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roleService = new SysRoleService();
        ReflectionTestUtils.setField(roleService, "roleMapper", roleMapper);
        ReflectionTestUtils.setField(roleService, "roleMenuMapper", roleMenuMapper);
        ReflectionTestUtils.setField(roleService, "userRoleMapper", userRoleMapper);
        ReflectionTestUtils.setField(roleService, "menuService", menuService);
    }

    @Test
    void nonAdminRoleListIsFilteredByCreator() {
        PageParam pageParam = new PageParam();
        RoleQueryRequest request = new RoleQueryRequest();
        when(roleMapper.selectPage(pageParam, request, "operator"))
                .thenReturn(new PageResult<SysRoleEntity>(List.of(), 0L));

        roleService.selectPage(pageParam, request, "operator", false);

        verify(roleMapper).selectPage(pageParam, request, "operator");
    }

    @Test
    void nonAdminRoleOptionsAreFilteredByCreator() {
        when(roleMapper.selectList()).thenReturn(List.of(
                role(1L, "operator"),
                role(2L, "another")));

        List<SysRoleEntity> roles = roleService.selectRoleAll("operator", false);

        verify(roleMapper).selectList();
        org.junit.jupiter.api.Assertions.assertEquals(1, roles.size());
        org.junit.jupiter.api.Assertions.assertEquals(1L, roles.get(0).getRoleId());
    }

    @Test
    void nonAdminCannotReadRoleCreatedByAnotherUser() {
        SysRoleEntity role = role(1L, "owner");
        when(roleMapper.selectList()).thenReturn(List.of(role));

        assertThrows(AbstractBusinessException.class,
                () -> roleService.selectRoleById(1L, "operator", false));
    }

    @Test
    void nonAdminCannotAssignMenuOutsideOwnMenus() {
        SysRoleEntity role = role(1L, "operator");
        when(roleMapper.selectList()).thenReturn(List.of(role));

        SysMenu ownedMenu = new SysMenu();
        ownedMenu.setMenuId(10L);
        when(menuService.selectMenuList(2L)).thenReturn(List.of(ownedMenu));

        RoleMenuUpdateRequest request = new RoleMenuUpdateRequest();
        request.setMenuIds(new Long[] {20L});

        assertThrows(AbstractBusinessException.class,
                () -> roleService.updateRoleMenus(1L, request, "operator", false, 2L));

        verify(roleMapper, never()).updateById(any(SysRoleEntity.class));
        verify(roleMenuMapper, never()).deleteByRoleId(1L);
    }

    @Test
    void adminCanReadRoleCreatedByAnotherUser() {
        SysRoleEntity role = role(1L, "owner");
        when(roleMapper.selectList()).thenReturn(List.of(role));

        roleService.selectRoleById(1L, "admin", true);

        verify(roleMapper).selectList();
    }

    private SysRoleEntity role(Long roleId, String createBy) {
        SysRoleEntity role = new SysRoleEntity();
        role.setRoleId(roleId);
        role.setCreateBy(createBy);
        return role;
    }
}
