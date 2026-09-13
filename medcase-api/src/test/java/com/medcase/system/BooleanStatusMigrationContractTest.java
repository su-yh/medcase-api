package com.medcase.system;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.medcase.biz.domain.SupplierEntity;
import com.medcase.biz.request.SupplierQuery;
import com.medcase.biz.request.SupplierSaveRequest;
import com.medcase.biz.request.SupplierStatusRequest;
import com.medcase.biz.response.SupplierResponse;
import com.medcase.system.entity.SysDeptEntity;
import com.medcase.system.entity.SysRoleEntity;
import com.medcase.system.entity.SysUserEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BooleanStatusMigrationContractTest {

    private static final Path SYSTEM_SCHEMA = Path.of(
            "src/main/resources/db/migration/master/V01_00_00/"
                    + "V01_00_00_001__system-schema.sql");
    private static final Path BUSINESS_SCHEMA = Path.of(
            "src/main/resources/db/migration/master/V01_00_00/"
                    + "V01_00_00_002__business-schema.sql");
    private static final Path USER_DETAILS_SERVICE = Path.of(
            "src/main/java/com/medcase/framework/web/service/UserDetailsServiceImpl.java");

    @Test
    void departmentDeleteFlagUsesBooleanLogicDelete() throws Exception {
        Field field = SysDeptEntity.class.getDeclaredField("delFlag");
        TableLogic tableLogic = field.getAnnotation(TableLogic.class);

        assertEquals(Boolean.class, field.getType());
        assertNotNull(tableLogic);
        assertEquals("0", tableLogic.value());
        assertEquals("1", tableLogic.delval());
    }

    @Test
    void systemDeleteFlagsUseBooleanFields() throws Exception {
        assertBooleanField(SysUserEntity.class, "delFlag");
        assertBooleanField(SysRoleEntity.class, "delFlag");
    }

    @Test
    void supplierStatusUsesBooleanAcrossApiTypes() throws Exception {
        assertBooleanField(SupplierEntity.class, "status");
        assertBooleanField(SupplierQuery.class, "status");
        assertBooleanField(SupplierSaveRequest.class, "status");
        assertBooleanField(SupplierStatusRequest.class, "status");
        assertBooleanField(SupplierResponse.class, "status");
    }

    @Test
    void schemasUseZeroOneDeleteAndSupplierBooleanStorage() throws Exception {
        String systemSql = Files.readString(SYSTEM_SCHEMA);
        String businessSql = Files.readString(BUSINESS_SCHEMA);

        assertTrue(systemSql.contains(
                "del_flag        tinyint      default 0 comment '逻辑删除（1删除，0未删除）'"));
        assertTrue(systemSql.contains(
                "status                   char(1)       default '0' comment '账号状态（0正常 1停用 3待审核 4审核失败 5注册）'"));
        assertTrue(systemSql.contains(
                "del_flag                 tinyint      default 0 comment '逻辑删除（1删除，0未删除）'"));
        assertTrue(systemSql.contains(
                "del_flag            tinyint      default 0 comment '逻辑删除（1删除，0未删除）'"));
        assertTrue(businessSql.contains(
                "status          tinyint      not null default 1 comment '状态（1正常 0停用）'"));
        assertFalse(businessSql.contains("status          char(1)"));
    }

    @Test
    void booleanDeleteFlagConversionChecksNullWithoutEquals() throws Exception {
        String source = Files.readString(USER_DETAILS_SERVICE);

        assertFalse(source.contains("user.getDelFlag().equals"));
        assertTrue(source.contains("user.getDelFlag() != null && user.getDelFlag()"));
    }

    private void assertBooleanField(Class<?> type, String fieldName) throws NoSuchFieldException {
        assertEquals(Boolean.class, type.getDeclaredField(fieldName).getType());
    }
}
