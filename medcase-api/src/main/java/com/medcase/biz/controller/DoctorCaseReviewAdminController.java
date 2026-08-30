package com.medcase.biz.controller;

import com.medcase.mp.mybatis.PageResult;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.biz.request.DoctorCaseReviewRequest;
import com.medcase.biz.request.DoctorCaseReviewQuery;
import com.medcase.biz.response.DoctorCaseReviewVO;
import com.medcase.biz.service.DoctorCaseReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 病例审核
 *
 * @author suyh
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/biz/case-review")
public class DoctorCaseReviewAdminController {
    private final DoctorCaseReviewService doctorCaseReviewService;

    @PreAuthorize("@ss.hasPermi('case:review:list')")
    @GetMapping("/list")
    public PageResult<DoctorCaseReviewVO> list(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            DoctorCaseReviewQuery query) {
        return doctorCaseReviewService.page(pageNum, pageSize, query);
    }

    @PreAuthorize("@ss.hasPermi('case:review:query')")
    @GetMapping("/{id}")
    public DoctorCaseReviewVO getInfo(@PathVariable Long id) {
        return doctorCaseReviewService.detail(id);
    }

    @PreAuthorize("@ss.hasPermi('case:review:review')")
    @PostMapping("/{id}/review")
    public void review(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @PathVariable Long id,
            @Valid @RequestBody DoctorCaseReviewRequest request) {
        doctorCaseReviewService.review(id, request, adminUser);
    }

    @PreAuthorize("@ss.hasPermi('case:review:settle')")
    @PostMapping("/{id}/settle")
    public void settle(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @PathVariable Long id) {
        doctorCaseReviewService.settle(id, adminUser);
    }
}
