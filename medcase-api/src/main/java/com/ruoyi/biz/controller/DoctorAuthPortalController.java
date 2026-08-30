package com.ruoyi.biz.controller;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.biz.request.DoctorLoginRequest;
import com.ruoyi.biz.request.DoctorRegisterRequest;
import com.ruoyi.biz.request.DoctorRegisterSmsCodeRequest;
import com.ruoyi.biz.service.DoctorAuthService;
import com.ruoyi.biz.service.DoctorRegisterSmsCodeService;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mvc.authentication.annotation.CurrLoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医生端认证接口
 *
 * @author suyh
 * @since 2026-08-19
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/biz/doctor-auth")
public class DoctorAuthPortalController {
    private final DoctorAuthService doctorAuthService;

    private final DoctorRegisterSmsCodeService smsCodeService;

    @Anonymous
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody DoctorLoginRequest loginBody) {
        log.trace("doctor auth controller login");
        return doctorAuthService.login(loginBody);
    }

    @Anonymous
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@RequestBody DoctorRegisterRequest registerBody) {
        log.trace("doctor auth controller register");
        doctorAuthService.register(registerBody);
    }

    @Anonymous
    @RequestMapping(value = "/register/sms-code", method = RequestMethod.POST)
    public void sendRegisterSmsCode(@RequestBody DoctorRegisterSmsCodeRequest request) {
        smsCodeService.sendCode(request.getPhone());
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout(@CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser) {
        log.trace("doctor auth controller logout");
        doctorAuthService.logout(doctorUser);
    }

    @DeleteMapping("/account")
    public void deleteAccount(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser) {
        log.trace("doctor auth controller delete account, userId={}", doctorUser.getUserId());
        doctorAuthService.deleteAccount(doctorUser);
    }
}
