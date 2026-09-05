package com.medcase.web.controller.system;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SysUserControllerTest {

    @Test
    void separatesNewUserAndExistingUserDetailRoutes() {
        long routeHandlerCount = Arrays.stream(SysUserController.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(GetMapping.class))
                .filter(method -> {
                    String[] routes = method.getAnnotation(GetMapping.class).value();
                    return Arrays.asList(routes).contains("/")
                            || Arrays.asList(routes).contains("/{userId}");
                })
                .count();

        assertEquals(2, routeHandlerCount);
        Arrays.stream(SysUserController.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(GetMapping.class))
                .filter(method -> {
                    String[] routes = method.getAnnotation(GetMapping.class).value();
                    return Arrays.asList(routes).contains("/")
                            || Arrays.asList(routes).contains("/{userId}");
                })
                .forEach(method -> assertEquals(
                        1, method.getAnnotation(GetMapping.class).value().length));
        assertTrue(Arrays.stream(SysUserController.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(GetMapping.class))
                .anyMatch(method -> Arrays.asList(method.getAnnotation(GetMapping.class).value()).contains("/")));
        assertTrue(Arrays.stream(SysUserController.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(GetMapping.class))
                .anyMatch(method -> Arrays.asList(method.getAnnotation(GetMapping.class).value())
                        .contains("/{userId}")));
    }

    @Test
    void userListUsesUnifiedPageParameter() throws NoSuchMethodException {
        Method method = SysUserController.class.getMethod(
                "list", PageParam.class, SysUser.class, String.class, String.class);
        assertEquals(PageResult.class, method.getReturnType());
        ParameterizedType pageResultType = (ParameterizedType) method.getGenericReturnType();
        assertEquals(SysUser.class.getTypeName(), pageResultType.getActualTypeArguments()[0].getTypeName());
    }
}
