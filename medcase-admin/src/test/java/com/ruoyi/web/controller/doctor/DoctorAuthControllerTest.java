package com.ruoyi.web.controller.doctor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.core.domain.model.DoctorLoginResponse;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.framework.web.service.DoctorAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class DoctorAuthControllerTest {
    private DoctorLoginController loginController;

    private DoctorRegisterController registerController;

    @Mock
    private DoctorAuthService doctorAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginController = new DoctorLoginController();
        registerController = new DoctorRegisterController();
        ReflectionTestUtils.setField(loginController, "doctorAuthService", doctorAuthService);
        ReflectionTestUtils.setField(registerController, "doctorAuthService", doctorAuthService);
    }

    @Test
    void loginReturnsToken() {
        LoginBody loginBody = new LoginBody();
        when(doctorAuthService.login(loginBody)).thenReturn(new DoctorLoginResponse("doctor-token"));

        DoctorLoginResponse response = loginController.login(loginBody);

        assertEquals("doctor-token", response.getToken());
        verify(doctorAuthService).login(loginBody);
    }

    @Test
    void registerDelegatesToService() {
        RegisterBody registerBody = new RegisterBody();

        registerController.register(registerBody);

        verify(doctorAuthService).register(registerBody);
    }
}
