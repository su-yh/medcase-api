package com.ruoyi.web.controller.doctor;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.mvc.authentication.annotation.CurrLoginUser;
import com.ruoyi.web.controller.doctor.request.DoctorCasePageRequest;
import com.ruoyi.web.controller.doctor.request.DoctorCaseSubmitRequest;
import com.ruoyi.web.controller.doctor.response.DoctorCaseVO;
import com.ruoyi.web.service.DoctorCaseService;
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
@RequestMapping(value = "/doctor")
@Validated
public class DoctorCaseController {
    private final DoctorCaseService doctorCaseService;

    @RequestMapping(value = "/cases", method = RequestMethod.POST)
    public void submit(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser loginUser,
            @RequestBody DoctorCaseSubmitRequest request) {
        log.trace("doctor case controller submit");
        doctorCaseService.submit(loginUser, request);
    }

    @RequestMapping(value = "/cases/draft", method = RequestMethod.POST)
    public void saveDraft(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser loginUser,
            @RequestBody DoctorCaseSubmitRequest request) {
        log.trace("doctor case controller saveDraft, request={}", request);
        doctorCaseService.saveDraft(loginUser, request);
    }

    @RequestMapping(value = "/cases", method = RequestMethod.GET)
    public PageResult<DoctorCaseVO> page(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser loginUser,
            PageParam pageParam, DoctorCasePageRequest request) {
        log.trace("doctor case controller page, request={}", request);
        return doctorCaseService.page(loginUser, pageParam, request);
    }

    @RequestMapping(value = "/cases/{id}", method = RequestMethod.GET)
    public DoctorCaseVO detail(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser loginUser,
            @PathVariable Long id) {
        log.trace("doctor case controller detail, id={}", id);
        return doctorCaseService.detail(loginUser, id);
    }
}

