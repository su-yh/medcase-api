package com.ruoyi.mvc.authentication;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatusEnums;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mvc.authentication.annotation.CurrLoginUser;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

class LoginUserArgumentResolverTest {
    private final LoginUserArgumentResolver resolver = new LoginUserArgumentResolver();

    @Test
    void currentUserAnnotationDefaultsToAllUserTypes() throws Exception {
        Method userType = CurrLoginUser.class.getMethod("userType");

        assertEquals(UserTypeEnums[].class, userType.getReturnType());
        assertArrayEquals(new UserTypeEnums[0], (UserTypeEnums[]) userType.getDefaultValue());
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void resolvesRegisteredUserWithoutCheckingStatus() throws Exception {
        loginAs(UserStatusEnums.REGISTER);

        LoginUser registeredUser = (LoginUser) resolve("currentUser");

        assertEquals(UserStatusEnums.REGISTER.getCode(), registeredUser.getUser().getStatus());
    }

    @Test
    void resolvesCurrentUserWithoutUserTypeRestriction() throws Exception {
        loginAs(UserStatusEnums.OK);

        LoginUser currentUser = (LoginUser) resolve("currentUserWithoutUserType");

        assertEquals(UserTypeEnums.DOCTOR, currentUser.getUser().getUserType());
    }

    @Test
    void resolvesCurrentUserWhenAnyConfiguredUserTypeMatches() throws Exception {
        loginAs(UserStatusEnums.OK);

        LoginUser currentUser = (LoginUser) resolve("currentUserForAdminOrDoctor");

        assertEquals(UserTypeEnums.DOCTOR, currentUser.getUser().getUserType());
    }

    private void loginAs(UserStatusEnums status) {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(status.getCode());

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUser(user);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities()));
    }

    private MethodParameter parameter(String methodName) throws NoSuchMethodException {
        Method method = TestController.class.getDeclaredMethod(methodName, LoginUser.class);
        return new MethodParameter(method, 0);
    }

    private Object resolve(String methodName) throws Exception {
        return resolver.resolveArgument(
                parameter(methodName),
                null,
                new ServletWebRequest(new MockHttpServletRequest()),
                null);
    }

    private static class TestController {
        void currentUser(@CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser loginUser) {
        }

        void currentUserWithoutUserType(@CurrLoginUser LoginUser loginUser) {
        }

        void currentUserForAdminOrDoctor(
                @CurrLoginUser(userType = {UserTypeEnums.ADMIN, UserTypeEnums.DOCTOR}) LoginUser loginUser) {
        }
    }
}
