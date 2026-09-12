package com.medcase.system;

import com.medcase.biz.domain.UserEntity;
import com.medcase.system.entity.SysUserEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRecordMigrationContractTest {

    private static final Path LOGIN_RECORD_MIGRATION = Path.of(
            "src/main/resources/db/migration/master/V01_01_00/"
                    + "V01_01_00_011__login-record.sql");

    @Test
    void loginRecordMigrationCreatesNewTableAndRemovesOldTable() throws Exception {
        assertTrue(Files.exists(LOGIN_RECORD_MIGRATION));
        String sql = Files.readString(LOGIN_RECORD_MIGRATION);
        assertTrue(sql.contains("create table sys_login_record"));
        assertTrue(sql.contains("drop table if exists sys_logininfor"));
    }

    @Test
    void userEntitiesDoNotContainLastLoginFields() {
        assertFalse(hasField(SysUserEntity.class, "loginIp"));
        assertFalse(hasField(SysUserEntity.class, "loginDate"));
        assertFalse(hasField(UserEntity.class, "loginIp"));
        assertFalse(hasField(UserEntity.class, "loginDate"));
    }

    @Test
    void loginRecordEntityExists() throws Exception {
        assertTrue(Class.forName("com.medcase.system.entity.LoginRecordEntity") != null);
    }

    @Test
    void loginRecordStatusUsesBooleanAndTinyint() throws Exception {
        Field status = Class.forName("com.medcase.system.entity.LoginRecordEntity")
                .getDeclaredField("status");
        assertEquals(Boolean.class, status.getType());

        Path statusMigration = Path.of(
                "src/main/resources/db/migration/master/V01_01_00/"
                        + "V01_01_00_012__login-record-status.sql");
        assertTrue(Files.exists(statusMigration));
        String sql = Files.readString(statusMigration);
        assertTrue(sql.contains("status tinyint"));
        assertTrue(sql.contains("1成功 0失败"));
        assertTrue(sql.contains("when 0 then 1"));
        assertTrue(sql.contains("when 1 then 0"));
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
