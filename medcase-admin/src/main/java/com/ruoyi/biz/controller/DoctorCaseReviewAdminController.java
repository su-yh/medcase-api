package com.ruoyi.biz.controller;

import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.biz.request.DoctorCaseReviewRequest;
import com.ruoyi.biz.request.DoctorCaseReviewQuery;
import com.ruoyi.biz.response.DoctorCaseReviewVO;
import com.ruoyi.biz.service.DoctorCaseReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.ruoyi.mvc.authentication.annotation.CurrLoginUser;
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

    @PreAuthorize("@ss.hasPermi('case:review:list')")
    @GetMapping("/{id}")
    public DoctorCaseReviewVO getInfo(@PathVariable Long id) {
        return doctorCaseReviewService.detail(id);
    }

    @PreAuthorize("@ss.hasPermi('case:review:list')")
    @PostMapping("/{id}/review")
    public void review(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @PathVariable Long id,
            @Valid @RequestBody DoctorCaseReviewRequest request) {
        doctorCaseReviewService.review(id, request, adminUser);
    }

    @PreAuthorize("@ss.hasPermi('case:review:list')")
    @PostMapping("/{id}/settle")
    public void settle(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @PathVariable Long id) {
        doctorCaseReviewService.settle(id, adminUser);
    }
}
