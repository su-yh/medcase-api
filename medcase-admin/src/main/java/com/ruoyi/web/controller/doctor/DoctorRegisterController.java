package com.ruoyi.web.controller.doctor;

import com.ruoyi.common.core.domain.model.RegisterBody;
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
public class DoctorRegisterController {
    @Autowired
    private DoctorAuthService doctorAuthService;

    @PostMapping("/doctor/register")
    public void register(@RequestBody RegisterBody registerBody) {
        doctorAuthService.register(registerBody);
    }
}
