package com.medcase.system.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.entity.SysRoleMenuEntity;
import com.medcase.system.mapper.SysRoleMapper;
import com.medcase.system.mapper.SysRoleMenuMapper;
import com.medcase.system.mapper.SysUserRoleMapper;
import com.medcase.web.controller.system.dto.RoleAddRequest;
import com.medcase.web.controller.system.dto.RoleEditRequest;
import com.medcase.web.controller.system.dto.RoleMenuUpdateRequest;
import com.medcase.web.controller.system.dto.RoleStatusRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
        RoleAddRequest addRole = new RoleAddRequest();
        roleService.insertRole(addRole, "admin");
        roleService.selectRoleAll();

        verify(roleMapper, times(2)).selectList();
    }

    @Test
    void insertRoleBuildsEntityFromRequestInService() {

        when(roleMapper.selectRoleByName("审核员")).thenReturn(null);
        when(roleMapper.selectRoleByKey("reviewer")).thenReturn(null);
        when(roleMapper.insert(any(SysRoleEntity.class))).thenReturn(1);

        RoleAddRequest request = new RoleAddRequest();
        request.setRoleName("审核员");
        request.setRoleKey("reviewer");
        request.setRoleSort(1);
        request.setMenuCheckStrictly(true);
        request.setStatus("0");
        request.setRemark("病例审核角色");

        roleService.insertRole(request, "admin");

        org.mockito.ArgumentCaptor<SysRoleEntity> captor = forClass(SysRoleEntity.class);
        verify(roleMapper).insert(captor.capture());
        SysRoleEntity role = captor.getValue();
        assertEquals("审核员", role.getRoleName());
        assertEquals("reviewer", role.getRoleKey());
        assertEquals(1, role.getRoleSort());
        assertEquals(Boolean.TRUE, role.getMenuCheckStrictly());
        assertEquals("0", role.getStatus());
        assertEquals("病例审核角色", role.getRemark());
        assertEquals("admin", role.getCreateBy());
    }

    @Test
    void updateRoleClearsRoleCache() {

        SysRoleEntity role = role(1L, "管理员");
        when(roleMapper.selectList()).thenReturn(List.of(role), List.of(role));
        when(roleMapper.updateById(any(SysRoleEntity.class))).thenReturn(1);
        when(roleMenuMapper.deleteByRoleId(1L)).thenReturn(0);

        roleService.selectRoleAll();
        RoleEditRequest updateRole = new RoleEditRequest();
        updateRole.setRoleId(1L);
        roleService.updateRole(updateRole, "admin", true);
        roleService.selectRoleAll();

        verify(roleMapper, times(2)).selectList();
    }

    @Test
    void updateRoleDoesNotChangeRoleMenus() {

        when(roleMapper.selectList()).thenReturn(List.of(role(1L, "管理员")));
        when(roleMapper.updateById(any(SysRoleEntity.class))).thenReturn(1);

        RoleEditRequest request = new RoleEditRequest();
        request.setRoleId(1L);
        request.setRoleName("审核员");
        request.setRoleKey("reviewer");

        roleService.updateRole(request, "admin", true);

        verify(roleMenuMapper, never()).deleteByRoleId(1L);
        verify(roleMenuMapper, never()).insertRoleMenus(any());
    }

    @Test
    void updateRoleMenusReplacesMenuRelations() {

        when(roleMapper.selectList()).thenReturn(List.of(role(1L, "管理员")));
        when(roleMapper.updateById(any(SysRoleEntity.class))).thenReturn(1);
        RoleMenuUpdateRequest request = new RoleMenuUpdateRequest();
        request.setMenuIds(new Long[] {10L, 20L});
        request.setMenuCheckStrictly(true);

        roleService.updateRoleMenus(1L, request, "admin", true, 1L);

        org.mockito.ArgumentCaptor<SysRoleEntity> roleCaptor = forClass(SysRoleEntity.class);
        verify(roleMapper).updateById(roleCaptor.capture());
        assertEquals(Boolean.TRUE, roleCaptor.getValue().getMenuCheckStrictly());
        verify(roleMenuMapper).deleteByRoleId(1L);
        org.mockito.ArgumentCaptor<Collection> captor = forClass(Collection.class);
        verify(roleMenuMapper).insertRoleMenus(captor.capture());
        Collection<?> relations = captor.getValue();
        assertEquals(2, relations.size());
        assertEquals(10L, ((SysRoleMenuEntity) relations.toArray()[0]).getMenuId());
        assertEquals(20L, ((SysRoleMenuEntity) relations.toArray()[1]).getMenuId());
    }

    @Test
    void selectRoleMenuIdsReadsRoleMenuRelations() {

        when(roleMapper.selectList()).thenReturn(List.of(role(1L, "管理员")));
        when(roleMenuMapper.selectMenuIdsByRoleId(1L)).thenReturn(List.of(10L, 20L));

        assertEquals(List.of(10L, 20L),
                roleService.selectRoleMenuIds(1L, "admin", true));
        verify(roleMenuMapper).selectMenuIdsByRoleId(1L);
    }

    @Test
    void updateRoleStatusClearsRoleCache() {

        SysRoleEntity role = role(1L, "管理员");
        when(roleMapper.selectList()).thenReturn(List.of(role), List.of(role));
        when(roleMapper.updateById(any(SysRoleEntity.class))).thenReturn(1);

        roleService.selectRoleAll();
        RoleStatusRequest updateRole = new RoleStatusRequest();
        updateRole.setRoleId(1L);
        roleService.updateRoleStatus(updateRole, "admin", true);
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
        roleService.deleteRoleByIds(new Long[] {1L}, "admin", true);
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
