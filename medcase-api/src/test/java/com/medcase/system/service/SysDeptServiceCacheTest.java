package com.medcase.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.constant.UserConstants;
import com.medcase.system.entity.SysDeptEntity;
import com.medcase.system.mapper.SysDeptMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

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
}
