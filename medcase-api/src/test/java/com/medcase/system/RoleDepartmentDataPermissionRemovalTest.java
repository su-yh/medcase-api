package com.medcase.system;

import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.entity.SysRoleEntity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class RoleDepartmentDataPermissionRemovalTest {

    @Test
    void roleModelNoLongerExposesDepartmentDataPermissionFields() {
        assertThat(findField("data" + "Scope")).isNull();
        assertThat(findField("dept" + "CheckStrictly")).isNull();
        assertThat(findField("dept" + "Ids")).isNull();
    }

    @Test
    void roleDepartmentDataPermissionArtifactsAreRemoved() throws IOException {
        assertThat(loadClass("com.medcase.system.entity.Sys" + "RoleDeptEntity")).isNull();
        assertThat(loadClass("com.medcase.system.mapper.Sys" + "RoleDeptMapper")).isNull();
        assertThat(Files.exists(Path.of("src/main/java/com/medcase/common/annotation/Data" + "Scope.java")))
                .isFalse();
        assertThat(Files.exists(Path.of("src/main/java/com/medcase/framework/aspectj/Data" + "ScopeAspect.java")))
                .isFalse();
        assertThat(Files.exists(Path.of("src/main/java/com/medcase/web/controller/system/dto/Role" + "DeptTreeResponse.java")))
                .isFalse();

        String userMapper = Files.readString(Path.of("src/main/resources/mapper/system/SysUserMapper.xml"));
        String systemSql = Files.readString(Path.of(
                "src/main/resources/db/migration/master/V01_00_00/"
                        + "V01_00_00_001__system-schema.sql"));
        String roleDeptTable = "sys_" + "role_dept";
        String dataScopeColumn = "data_" + "scope";
        String deptStrictColumn = "dept_" + "check_strictly";

        assertThat(Files.exists(Path.of("src/main/resources/mapper/system/SysRoleMapper.xml"))).isFalse();
        assertThat(userMapper).doesNotContain(dataScopeColumn);
        assertThat(systemSql).doesNotContain(
                roleDeptTable, dataScopeColumn, deptStrictColumn,
                "sys_oper_log", "sys_logininfor");
    }

    @Test
    void departmentSelectionAndUserDepartmentRemainAvailable() throws IOException {
        assertThat(SysUserEntity.class.getDeclaredFields()).extracting(Field::getName).contains("deptId");
        String deptController = Files.readString(Path.of(
                "src/main/java/com/medcase/web/controller/system/SysDeptController.java"));
        String userController = Files.readString(Path.of(
                "src/main/java/com/medcase/web/controller/system/SysUserController.java"));
        String roleService = Files.readString(Path.of(
                "src/main/java/com/medcase/system/service/SysRoleService.java"));

        assertThat(deptController).contains("@RequestMapping(\"/system/dept\")");
        assertThat(userController).contains("@GetMapping(\"/deptTree\")");
        assertThat(roleService).contains("updateRoleMenus");
    }

    private Field findField(String fieldName) {
        try {
            return SysRoleEntity.class.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            return null;
        }
    }

    private Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
}
