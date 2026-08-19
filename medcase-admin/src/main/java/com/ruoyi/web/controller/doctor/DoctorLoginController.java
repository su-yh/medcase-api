package com.ruoyi.web.controller.doctor;

import com.ruoyi.common.core.domain.model.DoctorLoginResponse;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.framework.web.service.DoctorAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suyh
 * @since 2026-08-19
 */
@RestController
public class DoctorLoginController {
    @Autowired
    private DoctorAuthService doctorAuthService;

    @PostMapping("/doctor/login")
    public DoctorLoginResponse login(@RequestBody LoginBody loginBody) {
        return doctorAuthService.login(loginBody);
    }
}
