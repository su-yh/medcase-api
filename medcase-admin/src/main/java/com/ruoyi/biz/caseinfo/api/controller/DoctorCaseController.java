package com.ruoyi.biz.caseinfo.api.controller;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.validation.groups.ValidationGroups;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.mvc.authentication.annotation.CurrLoginUser;
import com.ruoyi.biz.caseinfo.request.DoctorCasePageRequest;
import com.ruoyi.biz.caseinfo.request.DoctorCaseSubmitRequest;
import com.ruoyi.biz.caseinfo.response.DoctorCaseVO;
import com.ruoyi.biz.caseinfo.service.DoctorCaseService;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 医生病例接口
 *
 * @author suyh
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/biz/doctor")
@Validated
public class DoctorCaseController {
    private final DoctorCaseService doctorCaseService;

    @RequestMapping(value = "/cases", method = RequestMethod.POST)
    public void submit(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser,
            @RequestBody @Validated({ValidationGroups.Doctor.Submit.class, Default.class}) DoctorCaseSubmitRequest request) {
        log.trace("doctor case controller submit");
        doctorCaseService.submit(doctorUser, request);
    }

    @RequestMapping(value = "/cases/draft", method = RequestMethod.POST)
    public void saveDraft(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser,
            @RequestBody DoctorCaseSubmitRequest request) {
        log.trace("doctor case controller saveDraft, request={}", request);
        doctorCaseService.saveDraft(doctorUser, request);
    }

    @RequestMapping(value = "/cases", method = RequestMethod.GET)
    public PageResult<DoctorCaseVO> page(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser,
            PageParam pageParam, DoctorCasePageRequest request) {
        log.trace("doctor case controller page, request={}", request);
        return doctorCaseService.page(doctorUser, pageParam, request);
    }

    @RequestMapping(value = "/cases/{id}", method = RequestMethod.GET)
    public DoctorCaseVO detail(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser,
            @PathVariable Long id) {
        log.trace("doctor case controller detail, id={}", id);
        return doctorCaseService.detail(doctorUser, id);
    }

    @RequestMapping(value = "/cases/{id}", method = RequestMethod.DELETE)
    public void delete(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser,
            @PathVariable Long id) {
        log.trace("doctor case controller delete, id={}", id);
        doctorCaseService.delete(doctorUser, id);
    }
}
