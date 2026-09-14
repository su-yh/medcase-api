package com.medcase.framework.config;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SecurityConfigTest {
    @Test
    void permitsErrorDispatchPath() {
        assertEquals("/error", SecurityConfig.ERROR_PATH);
    }

    @Test
    void doesNotRegisterSpringSecurityLogoutEndpoint() throws Exception {
        String source = Files.readString(Path.of("src/main/java/com/medcase/framework/config/SecurityConfig.java"));

        assertFalse(source.contains("logoutUrl"));
        assertFalse(source.contains("LogoutSuccessHandlerImpl"));
    }
}
