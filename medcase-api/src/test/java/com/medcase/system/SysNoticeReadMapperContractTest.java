package com.medcase.system;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SysNoticeReadMapperContractTest {
    @Test
    void readUserSearchUsesNickNameLikeOnly() throws IOException {
        String mapperXml;
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("mapper/system/SysNoticeReadMapper.xml")) {
            mapperXml = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertTrue(mapperXml.contains("#{nickNameLike}"));
        assertFalse(mapperXml.contains("#{searchValue}"));
        assertFalse(mapperXml.contains("u.user_name like"));
        assertTrue(mapperXml.contains("u.nick_name like concat('%', #{nickNameLike}, '%')"));
        assertTrue(mapperXml.contains("u.dept_id      as deptId"));
        assertFalse(mapperXml.contains("sys_dept"));
    }
}
