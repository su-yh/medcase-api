package com.medcase.framework.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.medcase.storage.pojo.FileAttachment;
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

    @Test
    void jacksonTypeHandlerShouldIgnoreUnknownProperties() {
        new MybatisPlusConfig();
        JacksonTypeHandler typeHandler = new JacksonTypeHandler(FileAttachment.class);

        FileAttachment attachment = (FileAttachment) typeHandler.parse(
                "{\"fileName\":\"20260827/report.pdf\",\"originalFilename\":\"report.pdf\",\"url\":null}");

        assertEquals("report.pdf", attachment.getOriginalFilename());
        assertNull(attachment.getFilePath());
    }
}
