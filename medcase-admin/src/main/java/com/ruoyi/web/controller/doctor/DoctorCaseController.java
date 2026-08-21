package com.ruoyi.web.controller.doctor;

import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.web.controller.doctor.request.DoctorCaseSubmitRequest;
import com.ruoyi.web.controller.doctor.response.DoctorCaseVO;
import com.ruoyi.web.enums.DoctorCaseStatusEnums;
import com.ruoyi.web.service.DoctorCaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医生病例接口
 *
 * @author suyh
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/doctor")
public class DoctorCaseController {
    private final DoctorCaseService doctorCaseService;

    @RequestMapping(value = "/cases", method = RequestMethod.POST)
    public DoctorCaseVO submit(@RequestBody DoctorCaseSubmitRequest request) {
        log.trace("doctor case controller submit");
        return doctorCaseService.submit(request);
    }

    @RequestMapping(value = "/cases", method = RequestMethod.GET)
    public PageResult<DoctorCaseVO> page(
            PageParam pageParam,
            @RequestParam(value = "status", required = false) DoctorCaseStatusEnums status) {
        log.trace("doctor case controller page, status={}", status);
        return doctorCaseService.page(pageParam, status);
    }

    @RequestMapping(value = "/cases/{id}", method = RequestMethod.GET)
    public DoctorCaseVO detail(@PathVariable Long id) {
        log.trace("doctor case controller detail, id={}", id);
        return doctorCaseService.detail(id);
    }
}
