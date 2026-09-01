package com.medcase.web.controller.system;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

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
}
