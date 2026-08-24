package com.ruoyi.biz.controller;

import com.ruoyi.biz.request.DoctorLoginRequest;
import com.ruoyi.biz.request.DoctorRegisterRequest;
import com.ruoyi.biz.service.DoctorAuthService;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoctorAuthPortalControllerTest {
    private DoctorAuthPortalController doctorController;

    @Mock
    private DoctorAuthService doctorAuthService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorController = new DoctorAuthPortalController(doctorAuthService);
    }

    @Test
    void authControllerUsesBizDoctorRoute() {
        RequestMapping mapping = DoctorAuthPortalController.class.getAnnotation(RequestMapping.class);

        assertNotNull(mapping);
        assertEquals("/biz/doctor-auth", mapping.value()[0]);
    }

    @Test
    void loginDelegatesToService() {
        DoctorLoginRequest loginRequest = new DoctorLoginRequest();
        when(doctorAuthService.login(loginRequest)).thenReturn("doctor-token");

        assertEquals("doctor-token", doctorController.login(loginRequest));

        verify(doctorAuthService).login(loginRequest);
    }

    @Test
    void registerDelegatesToService() {
        DoctorRegisterRequest registerRequest = new DoctorRegisterRequest();

        doctorController.register(registerRequest);

        verify(doctorAuthService).register(registerRequest);
    }

    @Test
    void logoutDelegatesCurrentDoctorToService() {
        LoginUser doctorUser = doctorLoginUser();

        doctorController.logout(doctorUser);

        verify(doctorAuthService).logout(doctorUser);
    }

    @Test
    void deleteAccountDelegatesCurrentDoctorToService() {
        LoginUser doctorUser = doctorLoginUser();

        doctorController.deleteAccount(doctorUser);

        verify(doctorAuthService).deleteAccount(doctorUser);
    }

    @Test
    void loginAndRegisterAreAnonymous() throws NoSuchMethodException {
        assertNotNull(DoctorAuthPortalController.class
                .getMethod("login", DoctorLoginRequest.class)
                .getAnnotation(Anonymous.class));
        assertNotNull(DoctorAuthPortalController.class
                .getMethod("register", DoctorRegisterRequest.class)
                .getAnnotation(Anonymous.class));
    }

    private LoginUser doctorLoginUser() {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("doctor01");
        user.setUserType(UserTypeEnums.DOCTOR);

        LoginUser doctorUser = new LoginUser();
        doctorUser.setUser(user);
        doctorUser.setUserId(user.getUserId());
        return doctorUser;
    }
}
