package com.medcase.system;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseEntityMigrationContractTest {

    @Test
    void legacySystemObjectsShouldBeRemoved() {
        List<String> legacyTypes = List.of(
                "com.medcase.common.core.domain.entity.SysUser",
                "com.medcase.common.core.domain.entity.SysDept",
                "com.medcase.common.core.domain.entity.SysMenu");

        for (String typeName : legacyTypes) {
            assertThrows(ClassNotFoundException.class, () -> Class.forName(typeName), typeName);
        }
    }

    @Test
    void userControllerShouldUseDedicatedRequestAndResponseTypes() throws Exception {
        Method list = Arrays.stream(
                        Class.forName("com.medcase.web.controller.system.SysUserController")
                                .getDeclaredMethods())
                .filter(method -> method.getName().equals("list"))
                .findFirst()
                .orElseThrow();

        assertEquals("com.medcase.web.controller.system.dto.UserQueryRequest",
                list.getParameterTypes()[1].getName());
        ParameterizedType pageResultType = (ParameterizedType) list.getGenericReturnType();
        assertEquals("com.medcase.system.entity.SysUserEntity",
                pageResultType.getActualTypeArguments()[0].getTypeName());
    }
}
