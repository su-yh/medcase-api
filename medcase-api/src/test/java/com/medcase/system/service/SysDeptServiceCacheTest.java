package com.medcase.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.util.List;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.constant.UserConstants;
import com.medcase.web.controller.system.dto.DeptQueryRequest;
import com.medcase.system.entity.SysDeptEntity;
import com.medcase.system.mapper.SysDeptMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import com.github.benmanes.caffeine.cache.Cache;

class SysDeptServiceCacheTest {

    private SysDeptService deptService;

    @Mock
    private SysDeptMapper deptMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        deptService = new SysDeptService();
        ReflectionTestUtils.setField(deptService, "deptMapper", deptMapper);
    }

    @Test
    void selectDeptByIdLoadsAllDepartmentsOnlyOnce() {

        SysDeptEntity dept = new SysDeptEntity();
        dept.setDeptId(1L);
        dept.setDeptName("总部");
        when(deptMapper.selectAllDepartments()).thenReturn(List.of(dept));

        SysDept first = deptService.selectDeptById(1L);
        SysDept second = deptService.selectDeptById(1L);

        assertEquals("总部", first.getDeptName());
        assertEquals("总部", second.getDeptName());
        verify(deptMapper, times(1)).selectAllDepartments();
    }

    @Test
    void selectDeptByIdReturnsNullWhenDepartmentsAreNull() {

        when(deptMapper.selectAllDepartments()).thenReturn(null);

        SysDept result = deptService.selectDeptById(1L);

        assertEquals(null, result);
        verify(deptMapper, times(1)).selectAllDepartments();
    }

    @Test
    void insertDeptClearsAllDepartmentCache() {

        SysDeptEntity cachedDept = new SysDeptEntity();
        cachedDept.setDeptId(1L);
        cachedDept.setDeptName("总部");
        when(deptMapper.selectAllDepartments()).thenReturn(List.of(cachedDept));

        SysDeptEntity parentDept = new SysDeptEntity();
        parentDept.setDeptId(1L);
        parentDept.setStatus(UserConstants.DEPT_NORMAL);
        parentDept.setAncestors("0");
        when(deptMapper.selectById(1L)).thenReturn(parentDept);
        when(deptMapper.insert(org.mockito.ArgumentMatchers.any(SysDeptEntity.class))).thenReturn(1);

        SysDept newDept = new SysDept();
        newDept.setParentId(1L);
        deptService.selectDeptById(1L);
        deptService.insertDept(newDept);
        deptService.selectDeptById(1L);

        verify(deptMapper, times(2)).selectAllDepartments();
    }

    @Test
    void updateDeptClearsAllDepartmentCache() {

        SysDeptEntity cachedDept = new SysDeptEntity();
        cachedDept.setDeptId(1L);
        when(deptMapper.selectAllDepartments()).thenReturn(List.of(cachedDept));
        when(deptMapper.selectById(1L)).thenReturn(cachedDept);
        when(deptMapper.updateById(org.mockito.ArgumentMatchers.any(SysDeptEntity.class))).thenReturn(1);

        SysDept dept = new SysDept();
        dept.setDeptId(1L);
        deptService.selectDeptById(1L);
        deptService.updateDept(dept);
        deptService.selectDeptById(1L);

        verify(deptMapper, times(2)).selectAllDepartments();
    }

    @Test
    void updateDeptSortClearsAllDepartmentCache() {

        SysDeptEntity cachedDept = new SysDeptEntity();
        cachedDept.setDeptId(1L);
        when(deptMapper.selectAllDepartments()).thenReturn(List.of(cachedDept));

        deptService.selectDeptById(1L);
        deptService.updateDeptSort(new String[] {"1"}, new String[] {"1"});
        deptService.selectDeptById(1L);

        verify(deptMapper, times(2)).selectAllDepartments();
    }

    @Test
    void deleteDeptClearsAllDepartmentCache() {

        SysDeptEntity cachedDept = new SysDeptEntity();
        cachedDept.setDeptId(1L);
        when(deptMapper.selectAllDepartments()).thenReturn(List.of(cachedDept));
        when(deptMapper.deleteDeptById(1L)).thenReturn(1);

        deptService.selectDeptById(1L);
        deptService.deleteDeptById(1L);
        deptService.selectDeptById(1L);

        verify(deptMapper, times(2)).selectAllDepartments();
    }

    @Test
    void selectDeptListUsesCachedDepartmentsAndAppliesQuery() {

        SysDeptEntity department = new SysDeptEntity();
        department.setDeptId(1L);
        department.setParentId(0L);
        department.setDeptName("研发部门");
        department.setStatus(UserConstants.DEPT_NORMAL);
        when(deptMapper.selectAllDepartments()).thenReturn(List.of(department));

        DeptQueryRequest query = new DeptQueryRequest();
        query.setDeptNameLike("研发");

        List<SysDept> result = deptService.selectDeptList(query);

        assertEquals(1, result.size());
        assertEquals("研发部门", result.get(0).getDeptName());
        verify(deptMapper, times(1)).selectAllDepartments();
    }

    @Test
    void selectDeptListReturnsAllDepartmentsWithoutRoleScopeFiltering() {

        SysDeptEntity currentDepartment = department(1L, 0L, "总部", "0");
        SysDeptEntity childDepartment = department(2L, 1L, "子部门", "0,1");
        SysDeptEntity otherDepartment = department(3L, 0L, "其他部门", "0");
        when(deptMapper.selectAllDepartments())
                .thenReturn(List.of(currentDepartment, childDepartment, otherDepartment));

        DeptQueryRequest query = new DeptQueryRequest();

        List<SysDept> result = deptService.selectDeptList(query);

        assertEquals(List.of(1L, 2L, 3L), result.stream().map(SysDept::getDeptId).toList());
    }

    @Test
    void selectDeptListReturnsAllDepartmentsWithoutLoginUser() {

        SysDeptEntity department = department(1L, 0L, "总部", "0");
        when(deptMapper.selectAllDepartments()).thenReturn(List.of(department));

        List<SysDept> result = deptService.selectDeptList(new DeptQueryRequest());

        assertEquals(List.of(1L), result.stream().map(SysDept::getDeptId).toList());
    }

    @Test
    void selectNormalChildrenDeptByIdUsesCachedDepartments() {

        SysDeptEntity currentDepartment = department(1L, 0L, "总部", "0");
        SysDeptEntity childDepartment = department(2L, 1L, "子部门", "0,1");
        SysDeptEntity grandchildDepartment = department(3L, 2L, "孙部门", "0,1,2");
        SysDeptEntity disabledDepartment = department(4L, 1L, "停用部门", "0,1");
        disabledDepartment.setStatus(UserConstants.DEPT_DISABLE);
        when(deptMapper.selectAllDepartments())
                .thenReturn(List.of(currentDepartment, childDepartment, grandchildDepartment,
                        disabledDepartment));

        int result = deptService.selectNormalChildrenDeptById(1L);

        assertEquals(2, result);
    }

    @Test
    void hasChildByDeptIdUsesCachedDepartments() {

        SysDeptEntity childDepartment = department(2L, 1L, "子部门", "0,1");
        when(deptMapper.selectAllDepartments()).thenReturn(List.of(childDepartment));

        boolean result = deptService.hasChildByDeptId(1L);

        assertTrue(result);
    }

    @Test
    void checkDeptNameUniqueUsesCachedDepartments() {

        SysDeptEntity existingDepartment = department(1L, 10L, "研发部门", "0,10");
        when(deptMapper.selectAllDepartments()).thenReturn(List.of(existingDepartment));

        SysDept dept = new SysDept();
        dept.setDeptId(2L);
        dept.setParentId(10L);
        dept.setDeptName("研发部门");

        boolean result = deptService.checkDeptNameUnique(dept);

        assertEquals(UserConstants.NOT_UNIQUE, result);
    }

    @Test
    void departmentCacheHasWriteExpiration() {

        Cache<String, List<SysDeptEntity>> cache =
                (Cache<String, List<SysDeptEntity>>) ReflectionTestUtils.getField(deptService, "deptCache");

        assertTrue(cache.policy().expireAfterWrite().isPresent());
    }

    private SysDeptEntity department(Long deptId, Long parentId, String name, String ancestors) {

        SysDeptEntity department = new SysDeptEntity();
        department.setDeptId(deptId);
        department.setParentId(parentId);
        department.setDeptName(name);
        department.setAncestors(ancestors);
        department.setStatus(UserConstants.DEPT_NORMAL);
        return department;
    }

}
