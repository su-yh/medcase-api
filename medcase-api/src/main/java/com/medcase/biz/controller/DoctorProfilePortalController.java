package com.medcase.biz.controller;

import com.medcase.biz.request.DoctorProfileSubmitRequest;
import com.medcase.biz.response.DoctorProfileVO;
import com.medcase.biz.service.DoctorProfileService;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医生端资料接口
 *
 * @author suyh
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/biz/doctor-profile")
public class DoctorProfilePortalController {
    private final DoctorProfileService doctorProfileService;

    @RequestMapping(method = RequestMethod.GET)
    public DoctorProfileVO me(@CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser) {
        return doctorProfileService.me(doctorUser);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void submit(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser,
            @RequestBody @Valid DoctorProfileSubmitRequest request) {
        doctorProfileService.submit(doctorUser, request);
    }
}
