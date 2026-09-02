package com.medcase.biz.controller;

import com.medcase.biz.request.CasePageRequest;
import com.medcase.biz.request.CaseSubmitRequest;
import com.medcase.biz.response.CaseVO;
import com.medcase.biz.service.CaseService;
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
 * 病例端病例接口
 *
 * @author suyh
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/biz/cases")
@Validated
public class CasePortalController {
    private final CaseService caseService;

    @RequestMapping(method = RequestMethod.POST)
    @PreAuthorize("@dp.hasAnyStatus(#user, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public void submit(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user,
            @RequestBody @Validated({ValidationGroups.Doctor.Submit.class, Default.class}) CaseSubmitRequest request) {
        log.trace("case controller submit");
        caseService.submit(user, request);
    }

    @RequestMapping(value = "/draft", method = RequestMethod.POST)
    @PreAuthorize("@dp.hasAnyStatus(#user, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public void saveDraft(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user,
            @RequestBody CaseSubmitRequest request) {
        log.trace("case controller saveDraft, request={}", request);
        caseService.saveDraft(user, request);
    }

    @RequestMapping(method = RequestMethod.GET)
    @PreAuthorize("@dp.hasAnyStatus(#user, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public PageResult<CaseVO> page(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user,
            PageParam pageParam, CasePageRequest request) {
        log.trace("case controller page, request={}", request);
        return caseService.page(user, pageParam, request);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @PreAuthorize("@dp.hasAnyStatus(#user, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public CaseVO detail(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user,
            @PathVariable Long id) {
        log.trace("case controller detail, id={}", id);
        return caseService.detail(user, id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @PreAuthorize("@dp.hasAnyStatus(#user, T(com.medcase.common.enums.UserStatusEnums).OK)")
    public void delete(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user,
            @PathVariable Long id) {
        log.trace("case controller delete, id={}", id);
        caseService.delete(user, id);
    }
}
