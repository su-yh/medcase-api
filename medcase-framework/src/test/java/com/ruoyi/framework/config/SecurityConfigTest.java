package com.ruoyi.framework.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SecurityConfigTest {
    @Test
    void permitsErrorDispatchPath() {
        assertEquals("/error", SecurityConfig.ERROR_PATH);
    }
}
