package com.medcase.biz.controller;

import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.biz.request.CaseReviewRequest;
import com.medcase.biz.request.CaseReviewQuery;
import com.medcase.biz.response.CaseReviewVO;
import com.medcase.biz.service.CaseReviewService;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医生病例管理。
 *
 * @author suyh
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/biz/doctor-case")
public class DoctorCaseReviewAdminController {
    private final CaseReviewService caseReviewService;

    @PreAuthorize("@ss.hasPermi('doctor:case:list')")
    @GetMapping("/list")
    public PageResult<CaseReviewVO> list(
            PageParam pageParam,
            CaseReviewQuery query) {
        return caseReviewService.page(pageParam, query, UserTypeEnums.DOCTOR);
    }

    @PreAuthorize("@ss.hasPermi('doctor:case:query')")
    @GetMapping("/{id}")
    public CaseReviewVO getInfo(@PathVariable Long id) {
        return caseReviewService.detail(id, UserTypeEnums.DOCTOR);
    }

    @PreAuthorize("@ss.hasPermi('doctor:case:review')")
    @PostMapping("/{id}/review")
    public void review(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @PathVariable Long id,
            @Valid @RequestBody CaseReviewRequest request) {
        caseReviewService.review(id, request, adminUser, UserTypeEnums.DOCTOR);
    }

    @PreAuthorize("@ss.hasPermi('doctor:case:settle')")
    @PostMapping("/{id}/settle")
    public void settle(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @PathVariable Long id) {
        caseReviewService.settle(id, adminUser, UserTypeEnums.DOCTOR);
    }
}
