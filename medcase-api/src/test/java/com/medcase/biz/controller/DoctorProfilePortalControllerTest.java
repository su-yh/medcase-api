package com.medcase.biz.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medcase.biz.request.DoctorProfileSubmitRequest;
import com.medcase.biz.response.DoctorProfileVO;
import com.medcase.biz.service.DoctorProfileService;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.bind.annotation.RequestMapping;

class DoctorProfilePortalControllerTest {
    private DoctorProfilePortalController doctorProfileController;

    @Mock
    private DoctorProfileService doctorProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorProfileController = new DoctorProfilePortalController(doctorProfileService);
    }

    @Test
    void profileControllerUsesIndependentDoctorProfileRoute() {
        RequestMapping mapping = DoctorProfilePortalController.class.getAnnotation(RequestMapping.class);

        assertNotNull(mapping);
        assertEquals("/biz/doctor-profile", mapping.value()[0]);
    }

    @Test
    void profileEndpointsDelegateToServiceForCurrentDoctor() {
        LoginUser doctorUser = doctorLoginUser();
        DoctorProfileVO profile = new DoctorProfileVO();
        profile.setNickName("张医生");
        DoctorProfileSubmitRequest request = new DoctorProfileSubmitRequest();
        when(doctorProfileService.me(doctorUser)).thenReturn(profile);

        DoctorProfileVO response = doctorProfileController.me(doctorUser);
        assertEquals(profile, response);
        doctorProfileController.submit(doctorUser, request);

        verify(doctorProfileService).me(doctorUser);
        verify(doctorProfileService).submit(doctorUser, request);
    }

    private LoginUser doctorLoginUser() {
        SysUser user = new SysUser();
        user.setUserId(12L);
        user.setUserType(UserTypeEnums.DOCTOR);
        LoginUser doctorUser = new LoginUser();
        doctorUser.setUserId(12L);
        doctorUser.setUser(user);
        return doctorUser;
    }
}
