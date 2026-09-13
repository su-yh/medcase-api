package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.web.controller.system.dto.UserQueryRequest;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;

class SysUserMapperPageTest {

    @BeforeAll
    static void initTableInfo() {
        MybatisConfiguration configuration = new MybatisConfiguration();
        MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, "test");
        assistant.setCurrentNamespace("test");
        TableInfoHelper.initTableInfo(assistant, SysUserEntity.class);
    }

    @Test
    void selectUserPageDelegatesToBaseMapperXPageAndReturnsPageResult() {
        SysUserMapper mapper = mock(SysUserMapper.class, CALLS_REAL_METHODS);
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(3);
        pageParam.setPageSize(20);

        UserQueryRequest user = new UserQueryRequest();
        user.setUserName("admin");

        SysUserEntity resultUser = new SysUserEntity();
        resultUser.setUserName(user.getUserName());
        PageResult<SysUserEntity> pageResult = new PageResult<>(List.of(resultUser), 5L);
        doReturn(pageResult).when(mapper).selectPage(
                any(PageParam.class), any(Wrapper.class));

        PageResult<SysUserEntity> result = mapper.selectUserPage(
                pageParam, user, null, "2026-09-01", "2026-09-05");

        verify(mapper).selectPage(any(PageParam.class), any(Wrapper.class));
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
