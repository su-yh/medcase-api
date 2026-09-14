package com.medcase.system;

import com.medcase.system.entity.SysUserEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRecordMigrationContractTest {

    private static final Path SYSTEM_SCHEMA = Path.of(
            "src/main/resources/db/migration/master/V01_00_00/"
                    + "V01_00_00_001__system-schema.sql");

    @Test
    void systemSchemaContainsFinalLoginRecordTable() throws Exception {
        assertTrue(Files.exists(SYSTEM_SCHEMA));
        String sql = Files.readString(SYSTEM_SCHEMA);
        assertTrue(sql.contains("create table sys_login_record"));
        assertTrue(sql.contains("success         tinyint"));
        assertTrue(sql.contains("1成功 0失败"));
        assertFalse(sql.contains("sys_logininfor"));
    }

    @Test
    void userEntitiesDoNotContainLastLoginFields() {
        assertFalse(hasField(SysUserEntity.class, "loginIp"));
        assertFalse(hasField(SysUserEntity.class, "loginDate"));
    }

    @Test
    void loginRecordEntityExists() throws Exception {
        assertTrue(Class.forName("com.medcase.system.entity.LoginRecordEntity") != null);
    }

    @Test
    void loginRecordSuccessUsesBooleanAndTinyint() throws Exception {
        Field success = Class.forName("com.medcase.system.entity.LoginRecordEntity")
                .getDeclaredField("success");
        assertEquals(Boolean.class, success.getType());
    }

    private boolean hasField(Class<?> type, String name) {
        try {
            Field field = type.getDeclaredField(name);
            return field != null;
        }
        catch (NoSuchFieldException e) {
            return false;
        }
    }
}
