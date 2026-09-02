package com.medcase.biz.controller;

import com.medcase.biz.request.CaseReviewQuery;
import com.medcase.biz.request.CaseReviewRequest;
import com.medcase.biz.response.CaseReviewVO;
import com.medcase.biz.service.CaseReviewService;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者病例管理。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/biz/patient-case")
public class PatientCaseReviewAdminController {
    private final CaseReviewService caseService;

    @PreAuthorize("@ss.hasPermi('patient:case:list')")
    @GetMapping("/list")
    public PageResult<CaseReviewVO> list(
            PageParam pageParam,
            CaseReviewQuery query) {
        return caseService.page(pageParam, query, UserTypeEnums.PATIENT);
    }

    @PreAuthorize("@ss.hasPermi('patient:case:query')")
    @GetMapping("/{id}")
    public CaseReviewVO getInfo(@PathVariable Long id) {
        return caseService.detail(id, UserTypeEnums.PATIENT);
    }

    @PreAuthorize("@ss.hasPermi('patient:case:review')")
    @PostMapping("/{id}/review")
    public void review(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @PathVariable Long id,
            @Valid @RequestBody CaseReviewRequest request) {
        caseService.review(id, request, adminUser, UserTypeEnums.PATIENT);
    }

    @PreAuthorize("@ss.hasPermi('patient:case:settle')")
    @PostMapping("/{id}/settle")
    public void settle(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @PathVariable Long id) {
        caseService.settle(id, adminUser, UserTypeEnums.PATIENT);
    }
}
