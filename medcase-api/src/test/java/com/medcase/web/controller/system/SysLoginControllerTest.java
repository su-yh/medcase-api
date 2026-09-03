package com.medcase.web.controller.system;

import com.medcase.common.core.domain.model.LoginBody;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.framework.web.service.UserLoginService;
import com.medcase.web.controller.system.dto.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysLoginControllerTest {
    @Test
    void loginDelegatesToSharedUserLoginServiceAsAdmin() {
        UserLoginService userLoginService = mock(UserLoginService.class);
        SysLoginController controller = new SysLoginController();
        ReflectionTestUtils.setField(controller, "userLoginService", userLoginService);
        LoginBody loginBody = loginBody();
        when(userLoginService.login(
                "admin", "secret123", "1234", "captcha-uuid", UserTypeEnums.ADMIN))
                .thenReturn("admin-token");

        LoginResponse response = controller.login(loginBody);

        assertEquals("admin-token", response.getToken());
        verify(userLoginService).login(
                "admin", "secret123", "1234", "captcha-uuid", UserTypeEnums.ADMIN);
    }

    private LoginBody loginBody() {
        LoginBody loginBody = new LoginBody();
        loginBody.setUsername("admin");
        loginBody.setPassword("secret123");
        loginBody.setCode("1234");
        loginBody.setUuid("captcha-uuid");
        return loginBody;
    }
}
