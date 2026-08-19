package com.ruoyi.framework.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.junit.jupiter.api.Test;

class MybatisPlusConfigTest {
    @Test
    void interceptorShouldUseMysqlPagination() {
        MybatisPlusInterceptor interceptor = new MybatisPlusConfig().mybatisPlusInterceptor();

        assertEquals(1, interceptor.getInterceptors().size());
        InnerInterceptor innerInterceptor = interceptor.getInterceptors().get(0);
        PaginationInnerInterceptor pagination = assertInstanceOf(PaginationInnerInterceptor.class, innerInterceptor);
        assertEquals(DbType.MYSQL, pagination.getDbType());
    }
}
