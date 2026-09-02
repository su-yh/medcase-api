package com.medcase.biz.controller;

import com.medcase.biz.request.UserLoginRequest;
import com.medcase.biz.request.UserRegisterRequest;
import com.medcase.biz.request.UserRegisterSmsCodeRequest;
import com.medcase.biz.service.UserAuthService;
import com.medcase.biz.service.UserRegisterSmsCodeService;
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
import jakarta.validation.Valid;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAuthPortalControllerTest {
    private UserAuthPortalController userController;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private UserRegisterSmsCodeService smsCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserAuthPortalController(userAuthService, smsCodeService);
    }

    @Test
    void authControllerUsesSingleBizCaseRoute() {
        RequestMapping mapping = UserAuthPortalController.class.getAnnotation(RequestMapping.class);

        assertNotNull(mapping);
        assertArrayEquals(new String[] {"/biz/user-auth"}, mapping.value());
    }

    @Test
    void loginDelegatesToService() {
        UserLoginRequest loginRequest = new UserLoginRequest();
        when(userAuthService.login(loginRequest)).thenReturn("doctor-token");

        assertEquals("doctor-token", userController.login(loginRequest));

        verify(userAuthService).login(loginRequest);
    }

    @Test
    void registerDelegatesToService() {
        UserRegisterRequest registerRequest = new UserRegisterRequest();

        userController.register(registerRequest);

        verify(userAuthService).register(registerRequest);
    }

    @Test
    void sendRegisterSmsCodeDelegatesToService() {
        UserRegisterSmsCodeRequest request = new UserRegisterSmsCodeRequest();
        request.setPhone("13800000000");

        userController.sendRegisterSmsCode(request);

        verify(smsCodeService).sendCode("13800000000");
    }

    @Test
    void logoutDelegatesCurrentDoctorToService() {
        LoginUser user = doctorLoginUser();

        userController.logout(user);

        verify(userAuthService).logout(user);
    }

    @Test
    void deleteAccountDelegatesCurrentDoctorToService() {
        LoginUser user = doctorLoginUser();

        userController.deleteAccount(user);

        verify(userAuthService).deleteAccount(user);
    }

    @Test
    void loginAndRegisterAreAnonymous() throws NoSuchMethodException {
        assertArrayEquals(new RequestMethod[] {RequestMethod.POST},
                UserAuthPortalController.class.getMethod("login", UserLoginRequest.class)
                        .getAnnotation(RequestMapping.class).method());
        assertArrayEquals(new RequestMethod[] {RequestMethod.POST},
                UserAuthPortalController.class.getMethod("register", UserRegisterRequest.class)
                        .getAnnotation(RequestMapping.class).method());
        assertArrayEquals(new RequestMethod[] {RequestMethod.POST},
                UserAuthPortalController.class.getMethod(
                                "sendRegisterSmsCode", UserRegisterSmsCodeRequest.class)
                        .getAnnotation(RequestMapping.class).method());
        assertNotNull(UserAuthPortalController.class.getMethod("login", UserLoginRequest.class).getAnnotation(Anonymous.class));
        assertNotNull(UserAuthPortalController.class.getMethod("register", UserRegisterRequest.class).getAnnotation(Anonymous.class));
        assertNotNull(UserAuthPortalController.class.getMethod(
                        "sendRegisterSmsCode", UserRegisterSmsCodeRequest.class)
                .getAnnotation(Anonymous.class));
    }

    @Test
    void registerBindsJsonRequestBody() throws NoSuchMethodException {
        Method register = UserAuthPortalController.class.getMethod(
                "register", UserRegisterRequest.class);

        assertNotNull(register.getParameters()[0].getAnnotation(RequestBody.class));
        assertNotNull(register.getParameters()[0].getAnnotation(Valid.class));
    }

    private LoginUser doctorLoginUser() {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(1L);
        sysUser.setUserName("doctor01");
        sysUser.setUserType(UserTypeEnums.DOCTOR);

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(sysUser);
        loginUser.setUserId(sysUser.getUserId());
        return loginUser;
    }
}
