package com.medcase.system.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.web.controller.system.dto.UserQueryRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SysUserMapperPageTest {

    @Test
    void selectPageDelegatesToMybatisPlusPageAndReturnsPageResult() {
        SysUserMapper mapper = mock(SysUserMapper.class, CALLS_REAL_METHODS);
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(3);
        pageParam.setPageSize(20);

        UserQueryRequest user = new UserQueryRequest();
        user.setUserName("admin");

        when(mapper.selectUserPage(
                any(Page.class), any(UserQueryRequest.class), nullable(Collection.class),
                nullable(String.class), nullable(String.class))).thenAnswer(invocation -> {
            Page<SysUserEntity> page = invocation.getArgument(0);
            UserQueryRequest query = invocation.getArgument(1);
            SysUserEntity resultUser = new SysUserEntity();
            resultUser.setUserName(query.getUserName());
            page.setTotal(5);
            page.setRecords(List.of(resultUser));
            return page;
        });

        PageResult<SysUserEntity> result = mapper.selectPage(
                pageParam, user, null, "2026-09-01", "2026-09-05");

        ArgumentCaptor<Page> pageCaptor = ArgumentCaptor.forClass(Page.class);
        verify(mapper).selectUserPage(
                pageCaptor.capture(), any(UserQueryRequest.class), nullable(Collection.class),
                nullable(String.class), nullable(String.class));
        assertEquals(3L, pageCaptor.getValue().getCurrent());
        assertEquals(20L, pageCaptor.getValue().getSize());
        assertEquals(5, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("admin", result.getList().get(0).getUserName());
    }

    @Test
    void userQueriesDoNotJoinSysDept() throws IOException {

        String mapperXml;
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("mapper/system/SysUserMapper.xml")) {
            mapperXml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertEquals(-1, mapperXml.indexOf("sys_dept"));
        assertEquals(-1, mapperXml.indexOf("find_in_set"));
        assertTrue(mapperXml.contains("collection=\"deptIds\""));
        assertEquals(-1, mapperXml.indexOf("user.params."));
    }
}
