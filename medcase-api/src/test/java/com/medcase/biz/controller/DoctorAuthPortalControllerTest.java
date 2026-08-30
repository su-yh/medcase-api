package com.medcase.biz.controller;

import com.medcase.biz.request.DoctorLoginRequest;
import com.medcase.biz.request.DoctorRegisterRequest;
import com.medcase.biz.request.DoctorRegisterSmsCodeRequest;
import com.medcase.biz.service.DoctorAuthService;
import com.medcase.biz.service.DoctorRegisterSmsCodeService;
import com.medcase.common.annotation.Anonymous;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoctorAuthPortalControllerTest {
    private DoctorAuthPortalController doctorController;

    @Mock
    private DoctorAuthService doctorAuthService;

    @Mock
    private DoctorRegisterSmsCodeService smsCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorController = new DoctorAuthPortalController(doctorAuthService, smsCodeService);
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
    void sendRegisterSmsCodeDelegatesToService() {
        DoctorRegisterSmsCodeRequest request = new DoctorRegisterSmsCodeRequest();
        request.setPhone("13800000000");

        doctorController.sendRegisterSmsCode(request);

        verify(smsCodeService).sendCode("13800000000");
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
        assertArrayEquals(new RequestMethod[] {RequestMethod.POST},
                DoctorAuthPortalController.class.getMethod("login", DoctorLoginRequest.class)
                        .getAnnotation(RequestMapping.class).method());
        assertArrayEquals(new RequestMethod[] {RequestMethod.POST},
                DoctorAuthPortalController.class.getMethod("register", DoctorRegisterRequest.class)
                        .getAnnotation(RequestMapping.class).method());
        assertArrayEquals(new RequestMethod[] {RequestMethod.POST},
                DoctorAuthPortalController.class.getMethod(
                                "sendRegisterSmsCode", DoctorRegisterSmsCodeRequest.class)
                        .getAnnotation(RequestMapping.class).method());
        assertNotNull(DoctorAuthPortalController.class.getMethod("login", DoctorLoginRequest.class).getAnnotation(Anonymous.class));
        assertNotNull(DoctorAuthPortalController.class.getMethod("register", DoctorRegisterRequest.class).getAnnotation(Anonymous.class));
        assertNotNull(DoctorAuthPortalController.class.getMethod(
                        "sendRegisterSmsCode", DoctorRegisterSmsCodeRequest.class)
                .getAnnotation(Anonymous.class));
    }

    @Test
    void registerBindsJsonRequestBody() throws NoSuchMethodException {
        Method register = DoctorAuthPortalController.class.getMethod(
                "register", DoctorRegisterRequest.class);

        assertNotNull(register.getParameterAnnotations()[0][0]);
        assertEquals(RequestBody.class,
                register.getParameterAnnotations()[0][0].annotationType());
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
