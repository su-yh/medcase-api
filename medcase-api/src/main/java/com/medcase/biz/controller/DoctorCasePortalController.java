package com.medcase.biz.controller;

import com.medcase.biz.request.DoctorCasePageRequest;
import com.medcase.biz.request.DoctorCaseSubmitRequest;
import com.medcase.biz.response.DoctorCaseVO;
import com.medcase.biz.service.DoctorCaseService;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.validation.groups.ValidationGroups;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 医生端病例接口
 *
 * @author suyh
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/biz/cases")
@Validated
public class DoctorCasePortalController {
    private final DoctorCaseService doctorCaseService;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("@dp.hasAnyStatus(#doctorUser, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public void submit(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser doctorUser,
            @RequestBody @Validated({ValidationGroups.Doctor.Submit.class, Default.class}) DoctorCaseSubmitRequest request) {
        log.trace("doctor case controller submit");
        doctorCaseService.submit(doctorUser, request);
    }

    @RequestMapping(value = "/draft", method = RequestMethod.POST)
    @PreAuthorize("@dp.hasAnyStatus(#doctorUser, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public void saveDraft(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser doctorUser,
            @RequestBody DoctorCaseSubmitRequest request) {
        log.trace("doctor case controller saveDraft, request={}", request);
        doctorCaseService.saveDraft(doctorUser, request);
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("@dp.hasAnyStatus(#doctorUser, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public PageResult<DoctorCaseVO> page(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser doctorUser,
            PageParam pageParam, DoctorCasePageRequest request) {
        log.trace("doctor case controller page, request={}", request);
        return doctorCaseService.page(doctorUser, pageParam, request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("@dp.hasAnyStatus(#doctorUser, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public DoctorCaseVO detail(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser doctorUser,
            @PathVariable Long id) {
        log.trace("doctor case controller detail, id={}", id);
        return doctorCaseService.detail(doctorUser, id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("@dp.hasAnyStatus(#doctorUser, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public void delete(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser doctorUser,
            @PathVariable Long id) {
        log.trace("doctor case controller delete, id={}", id);
        doctorCaseService.delete(doctorUser, id);
    }
}
