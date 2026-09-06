package com.medcase.web.controller.system;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.service.SysRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SysRoleControllerPageTest {

    @Test
    void allocatedAndUnallocatedUserRoutesUseUnifiedPageParameter() throws NoSuchMethodException {
        Method allocated = SysRoleController.class.getMethod("allocatedList", PageParam.class, SysUser.class);
        Method unallocated = SysRoleController.class.getMethod("unallocatedList", PageParam.class, SysUser.class);

        assertEquals(PageResult.class, allocated.getReturnType());
        assertEquals(PageResult.class, unallocated.getReturnType());

        ParameterizedType allocatedType = (ParameterizedType) allocated.getGenericReturnType();
        ParameterizedType unallocatedType = (ParameterizedType) unallocated.getGenericReturnType();
        assertEquals(SysUser.class.getTypeName(), allocatedType.getActualTypeArguments()[0].getTypeName());
        assertEquals(SysUser.class.getTypeName(), unallocatedType.getActualTypeArguments()[0].getTypeName());

        assertTrue(Arrays.asList(allocated.getAnnotation(GetMapping.class).value()).contains("/authUser/allocatedList"));
        assertTrue(Arrays.asList(unallocated.getAnnotation(GetMapping.class).value()).contains("/authUser/unallocatedList"));
    }

    @Test
    void requestBodyParametersDoNotUseSysRole() {
        for (Method method : SysRoleController.class.getDeclaredMethods()) {
            for (Parameter parameter : method.getParameters()) {
                if (parameter.isAnnotationPresent(RequestBody.class)) {
                    assertNotEquals(
                            "com.medcase.common.core.domain.entity.SysRole",
                            parameter.getType().getName(),
                            method.getName() + " request body should use request dto");
                }
            }
        }
    }

    @Test
    void roleServicePublicMethodsDoNotUseSysRoleParameter() {
        for (Method method : SysRoleService.class.getDeclaredMethods()) {
            if (!java.lang.reflect.Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            for (Parameter parameter : method.getParameters()) {
                assertNotEquals(
                        "com.medcase.common.core.domain.entity.SysRole",
                        parameter.getType().getName(),
                        method.getName() + " should not use SysRole in service layer");
            }
        }
    }
}
