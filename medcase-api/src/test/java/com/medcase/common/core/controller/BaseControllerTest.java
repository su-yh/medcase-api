package com.medcase.common.core.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class BaseControllerTest {

    @Test
    void shouldNotExposeResponseFactoryMethods() {

        Set<String> responseFactoryMethods = Set.of("success", "error", "warn", "toAjax");

        assertFalse(java.util.Arrays.stream(BaseController.class.getDeclaredMethods())
                .anyMatch(method -> responseFactoryMethods.contains(method.getName())));
    }

    @Test
    void shouldNotExposePageResultFactoryMethod() {

        assertFalse(java.util.Arrays.stream(BaseController.class.getDeclaredMethods())
                .anyMatch(method -> method.getName().equals("getPageResult")));
    }

    @Test
    void shouldOnlyExposeInitBinder() {

        Set<String> declaredMethodNames = java.util.Arrays.stream(BaseController.class.getDeclaredMethods())
                .map(Method::getName)
                .collect(Collectors.toSet());

        assertEquals(Set.of("initBinder"), declaredMethodNames);
    }
}
