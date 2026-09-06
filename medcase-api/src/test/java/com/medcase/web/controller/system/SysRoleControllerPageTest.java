package com.medcase.web.controller.system;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.service.SysRoleService;
import com.medcase.web.controller.system.dto.RoleAddRequest;
import com.medcase.web.controller.system.dto.RoleEditRequest;
import com.medcase.web.controller.system.dto.RoleMenuUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void roleMutationRequestsDoNotContainMenuIds() {
        assertThrows(NoSuchFieldException.class, () -> RoleAddRequest.class.getDeclaredField("menuIds"));
        assertThrows(NoSuchFieldException.class, () -> RoleEditRequest.class.getDeclaredField("menuIds"));
    }

    @Test
    void roleMenuEndpointsAreSeparateFromRoleMutation() throws NoSuchMethodException {
        Method select = SysRoleController.class.getMethod("getRoleMenuIds", Long.class);
        Method update = SysRoleController.class.getMethod("updateRoleMenus", Long.class, RoleMenuUpdateRequest.class);

        assertTrue(Arrays.asList(select.getAnnotation(GetMapping.class).value())
                .contains("/{roleId}/menuIds"));
        assertTrue(Arrays.asList(update.getAnnotation(PutMapping.class).value())
                .contains("/{roleId}/menus"));
        assertTrue(Arrays.stream(update.getParameters())
                .anyMatch(parameter -> parameter.isAnnotationPresent(RequestBody.class)));
    }

    @Test
    void roleMenuRequestIncludesMenuCheckStrictly() throws NoSuchFieldException {
        assertEquals(boolean.class,
                RoleMenuUpdateRequest.class.getDeclaredField("menuCheckStrictly").getType());
    }
}
