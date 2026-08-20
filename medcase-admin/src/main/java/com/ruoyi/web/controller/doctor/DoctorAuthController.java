package com.ruoyi.web.controller.doctor;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.web.controller.doctor.request.DoctorLoginRequest;
import com.ruoyi.web.controller.doctor.request.DoctorRegisterRequest;
import com.ruoyi.web.service.DoctorAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
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
@RequestMapping("/doctor/auth")
public class DoctorAuthController {
    private final DoctorAuthService doctorAuthService;

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
}
