package com.ruoyi.biz.controller;

import com.ruoyi.biz.request.DoctorProfileSubmitRequest;
import com.ruoyi.biz.response.DoctorProfileVO;
import com.ruoyi.biz.service.DoctorProfileService;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mvc.authentication.annotation.CurrLoginUser;
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
