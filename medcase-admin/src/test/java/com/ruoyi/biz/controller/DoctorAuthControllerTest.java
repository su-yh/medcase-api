package com.ruoyi.biz.controller;

import com.ruoyi.biz.request.DoctorLoginRequest;
import com.ruoyi.biz.request.DoctorRegisterRequest;
import com.ruoyi.biz.service.DoctorAuthService;
import com.ruoyi.common.annotation.Anonymous;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RequestMapping;

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
    void authControllerUsesBizDoctorRoute() {
        RequestMapping mapping = DoctorAuthController.class.getAnnotation(RequestMapping.class);

        assertNotNull(mapping);
        assertEquals("/biz/doctor-auth", mapping.value()[0]);
    }

    @Test
    void loginRegisterAndLogoutDelegateToService() {
        DoctorLoginRequest loginRequest = new DoctorLoginRequest();
        DoctorRegisterRequest registerRequest = new DoctorRegisterRequest();
        when(doctorAuthService.login(loginRequest)).thenReturn("doctor-token");

        assertEquals("doctor-token", doctorController.login(loginRequest));
        doctorController.register(registerRequest);
        doctorController.logout();

        verify(doctorAuthService).login(loginRequest);
        verify(doctorAuthService).register(registerRequest);
        verify(doctorAuthService).logout();
    }

    @Test
    void loginAndRegisterAreAnonymous() throws NoSuchMethodException {
        assertNotNull(DoctorAuthController.class
                .getMethod("login", DoctorLoginRequest.class)
                .getAnnotation(Anonymous.class));
        assertNotNull(DoctorAuthController.class
                .getMethod("register", DoctorRegisterRequest.class)
                .getAnnotation(Anonymous.class));
    }
}
