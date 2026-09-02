package com.medcase.biz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medcase.biz.request.UserProfileSubmitRequest;
import com.medcase.biz.response.UserProfileVO;
import com.medcase.biz.service.UserProfileService;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;

import java.lang.reflect.Method;

class UserProfilePortalControllerTest {
    private UserProfilePortalController userProfileController;

    @Mock
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userProfileController = new UserProfilePortalController(userProfileService);
    }

    @Test
    void profileControllerUsesSingleCaseProfileRoute() {
        RequestMapping mapping = UserProfilePortalController.class.getAnnotation(RequestMapping.class);

        assertNotNull(mapping);
        assertEquals("/biz/user-profile", mapping.value()[0]);
    }

    @Test
    void profileSubmitBindsValidatedRequest() throws NoSuchMethodException {
        Method submit = UserProfilePortalController.class.getMethod(
                "submit", LoginUser.class, UserProfileSubmitRequest.class);

        assertNotNull(submit.getParameters()[1].getAnnotation(Valid.class));
    }

    @Test
    void profileEndpointsDelegateToServiceForCurrentDoctor() {
        LoginUser user = userLoginUser();
        UserProfileVO profile = new UserProfileVO();
        profile.setNickName("张医生");
        UserProfileSubmitRequest request = new UserProfileSubmitRequest();
        when(userProfileService.me(user)).thenReturn(profile);

        UserProfileVO response = userProfileController.me(user);
        assertEquals(profile, response);
        userProfileController.submit(user, request);

        verify(userProfileService).me(user);
        verify(userProfileService).submit(user, request);
    }

    private LoginUser userLoginUser() {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(12L);
        sysUser.setUserType(UserTypeEnums.DOCTOR);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(12L);
        loginUser.setUser(sysUser);
        return loginUser;
    }
}
