package com.ruoyi.web.controller.doctor;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.web.controller.doctor.request.DoctorLoginRequest;
import com.ruoyi.web.controller.doctor.request.DoctorRegisterRequest;
import com.ruoyi.web.service.DoctorAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        DoctorLoginRequest loginRequest = new DoctorLoginRequest();
        when(doctorAuthService.login(loginRequest)).thenReturn("doctor-token");

        String response = doctorController.login(loginRequest);

        assertEquals("doctor-token", response);
        verify(doctorAuthService).login(loginRequest);
    }

    @Test
    void registerDelegatesToService() {
        DoctorRegisterRequest registerRequest = new DoctorRegisterRequest();

        doctorController.register(registerRequest);

        verify(doctorAuthService).register(registerRequest);
    }

    @Test
    void logoutDelegatesToService() {
        doctorController.logout();

        verify(doctorAuthService).logout();
    }

    @Test
    void loginAndRegisterAreAnonymous() throws NoSuchMethodException {
        assertNotNull(DoctorAuthController.class.getMethod("login", DoctorLoginRequest.class).getAnnotation(Anonymous.class));
        assertNotNull(DoctorAuthController.class.getMethod("register", DoctorRegisterRequest.class).getAnnotation(Anonymous.class));
    }
}
