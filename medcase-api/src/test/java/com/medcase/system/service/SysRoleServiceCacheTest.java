package com.medcase.system.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.mapper.SysRoleMapper;
import com.medcase.system.mapper.SysRoleMenuMapper;
import com.medcase.system.mapper.SysUserRoleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysRoleServiceCacheTest {

    private SysRoleService roleService;

    @Mock
    private SysRoleMapper roleMapper;

    @Mock
    private SysRoleMenuMapper roleMenuMapper;

    @Mock
    private SysUserRoleMapper userRoleMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        roleService = new SysRoleService();
        ReflectionTestUtils.setField(roleService, "roleMapper", roleMapper);
        ReflectionTestUtils.setField(roleService, "roleMenuMapper", roleMenuMapper);
        ReflectionTestUtils.setField(roleService, "userRoleMapper", userRoleMapper);
    }

    @Test
    void selectRoleAllLoadsOnlyOnceAndReturnsCachedRoles() {

        SysRoleEntity role = role(1L, "管理员");
        when(roleMapper.selectList()).thenReturn(List.of(role));

        List<SysRoleEntity> first = roleService.selectRoleAll();
        List<SysRoleEntity> second = roleService.selectRoleAll();

        assertEquals(1, first.size());
        assertEquals(1, second.size());
        assertEquals("管理员", first.get(0).getRoleName());
        verify(roleMapper, times(1)).selectList();
    }

    @Test
    void selectRoleByIdUsesCachedRoles() {

        SysRoleEntity role = role(1L, "管理员");
        when(roleMapper.selectList()).thenReturn(List.of(role));

        SysRoleEntity first = roleService.selectRoleById(1L);
        SysRoleEntity second = roleService.selectRoleById(1L);

        assertEquals("管理员", first.getRoleName());
        assertEquals("管理员", second.getRoleName());
        verify(roleMapper, times(1)).selectList();
        verify(roleMapper, times(0)).selectById(1L);
    }

    @Test
    void insertRoleClearsRoleCache() {

        SysRoleEntity role = role(1L, "管理员");
        when(roleMapper.selectList()).thenReturn(List.of(role), List.of(role));
        when(roleMapper.insert(any(SysRoleEntity.class))).thenReturn(1);

        roleService.selectRoleAll();
        SysRole addRole = new SysRole();
        addRole.setMenuIds(new Long[0]);
        roleService.insertRole(addRole);
        roleService.selectRoleAll();

        verify(roleMapper, times(2)).selectList();
    }

    @Test
    void updateRoleClearsRoleCache() {

        SysRoleEntity role = role(1L, "管理员");
        when(roleMapper.selectList()).thenReturn(List.of(role), List.of(role));
        when(roleMapper.updateById(any(SysRoleEntity.class))).thenReturn(1);
        when(roleMenuMapper.deleteByRoleId(1L)).thenReturn(0);

        roleService.selectRoleAll();
        SysRole updateRole = new SysRole();
        updateRole.setRoleId(1L);
        updateRole.setMenuIds(new Long[0]);
        roleService.updateRole(updateRole);
        roleService.selectRoleAll();

        verify(roleMapper, times(2)).selectList();
    }

    @Test
    void updateRoleStatusClearsRoleCache() {

        SysRoleEntity role = role(1L, "管理员");
        when(roleMapper.selectList()).thenReturn(List.of(role), List.of(role));
        when(roleMapper.updateById(any(SysRoleEntity.class))).thenReturn(1);

        roleService.selectRoleAll();
        SysRole updateRole = new SysRole();
        updateRole.setRoleId(1L);
        roleService.updateRoleStatus(updateRole);
        roleService.selectRoleAll();

        verify(roleMapper, times(2)).selectList();
    }

    @Test
    void deleteRoleByIdsClearsRoleCache() {

        SysRoleEntity role = role(1L, "管理员");
        when(roleMapper.selectList()).thenReturn(List.of(role), List.of(role));
        when(userRoleMapper.countByRoleId(1L)).thenReturn(0L);
        when(roleMenuMapper.deleteByRoleIds(new Long[] {1L})).thenReturn(1);
        when(roleMapper.deleteRolesByIds(new Long[] {1L})).thenReturn(1);

        roleService.selectRoleAll();
        roleService.deleteRoleByIds(new Long[] {1L});
        roleService.selectRoleAll();

        verify(roleMapper, times(2)).selectList();
    }

    @Test
    void roleCacheExpiresAfterThirtyMinutes() {

        Cache<String, List<SysRoleEntity>> cache =
                (Cache<String, List<SysRoleEntity>>) ReflectionTestUtils.getField(roleService, "roleCache");

        assertTrue(cache.policy().expireAfterWrite().isPresent());
        assertEquals(30L, cache.policy().expireAfterWrite().get().getExpiresAfter(TimeUnit.MINUTES));
    }

    private SysRoleEntity role(Long roleId, String roleName) {

        SysRoleEntity role = new SysRoleEntity();
        role.setRoleId(roleId);
        role.setRoleName(roleName);
        role.setMenuCheckStrictly(Boolean.TRUE);
        return role;
    }
}
