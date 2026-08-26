package com.ruoyi.common.core.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import org.junit.jupiter.api.Test;

class BaseControllerTest
{
    @Test
    void shouldNotExposeResponseFactoryMethods()
    {
        Set<String> responseFactoryMethods = Set.of("success", "error", "warn", "toAjax");

        assertFalse(java.util.Arrays.stream(BaseController.class.getDeclaredMethods())
                .anyMatch(method -> responseFactoryMethods.contains(method.getName())));
    }
}
