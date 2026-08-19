package com.ruoyi.web.controller.doctor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.web.service.DoctorAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DoctorAuthControllerTest {
    private DoctorAuthController doctorController;

    @Mock
    private DoctorAuthService doctorAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorController = new DoctorAuthController(doctorAuthService);
    }

    @Test
    void loginReturnsToken() {
        LoginBody loginBody = new LoginBody();
        when(doctorAuthService.login(loginBody)).thenReturn("doctor-token");

        String response = doctorController.login(loginBody);

        assertEquals("doctor-token", response);
        verify(doctorAuthService).login(loginBody);
    }

    @Test
    void registerDelegatesToService() {
        RegisterBody registerBody = new RegisterBody();

        doctorController.register(registerBody);

        verify(doctorAuthService).register(registerBody);
    }

    @Test
    void loginAndRegisterAreAnonymous() throws NoSuchMethodException {
        assertNotNull(DoctorAuthController.class.getMethod("login", LoginBody.class).getAnnotation(Anonymous.class));
        assertNotNull(DoctorAuthController.class.getMethod("register", RegisterBody.class).getAnnotation(Anonymous.class));
    }
}
