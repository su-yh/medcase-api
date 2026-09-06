package com.medcase.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.web.controller.system.dto.UserQueryRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysUserMapperRolePageTest {

    @Test
    void selectAllocatedPageDelegatesToMybatisPlusPageAndReturnsPageResult() {
        SysUserMapper mapper = mock(SysUserMapper.class, CALLS_REAL_METHODS);
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(2);
        pageParam.setPageSize(15);

        UserQueryRequest user = new UserQueryRequest();
        user.setRoleId(99L);

        when(mapper.selectAllocatedPage(
                any(Page.class), any(UserQueryRequest.class), nullable(Collection.class))).thenAnswer(invocation -> {
            Page<SysUserEntity> page = invocation.getArgument(0);
            page.setTotal(9);
            SysUserEntity resultUser = new SysUserEntity();
            resultUser.setRoleId(user.getRoleId());
            page.setRecords(List.of(resultUser));
            return page;
        });

        PageResult<SysUserEntity> result = mapper.selectAllocatedPage(pageParam, user);

        ArgumentCaptor<Page> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(mapper).selectAllocatedPage(
                pageCaptor.capture(), any(UserQueryRequest.class), nullable(Collection.class));
        assertEquals(2L, pageCaptor.getValue().getCurrent());
        assertEquals(15L, pageCaptor.getValue().getSize());
        assertEquals(9, result.getTotal());
        assertEquals(99L, result.getList().get(0).getRoleId());
    }

    @Test
    void selectUnallocatedPageDelegatesToMybatisPlusPageAndReturnsPageResult() {
        SysUserMapper mapper = mock(SysUserMapper.class, CALLS_REAL_METHODS);
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(4);
        pageParam.setPageSize(8);

        UserQueryRequest user = new UserQueryRequest();
        user.setRoleId(100L);

        when(mapper.selectUnallocatedPage(
                any(Page.class), any(UserQueryRequest.class), nullable(Collection.class))).thenAnswer(invocation -> {
            Page<SysUserEntity> page = invocation.getArgument(0);
            page.setTotal(11);
            SysUserEntity resultUser = new SysUserEntity();
            resultUser.setRoleId(user.getRoleId());
            page.setRecords(List.of(resultUser));
            return page;
        });

        PageResult<SysUserEntity> result = mapper.selectUnallocatedPage(pageParam, user);

        ArgumentCaptor<Page> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(mapper).selectUnallocatedPage(
                pageCaptor.capture(), any(UserQueryRequest.class), nullable(Collection.class));
        assertEquals(4L, pageCaptor.getValue().getCurrent());
        assertEquals(8L, pageCaptor.getValue().getSize());
        assertEquals(11, result.getTotal());
        assertEquals(100L, result.getList().get(0).getRoleId());
    }
}
