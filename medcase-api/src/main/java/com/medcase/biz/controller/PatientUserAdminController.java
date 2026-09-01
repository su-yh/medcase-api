package com.medcase.biz.controller;

import com.medcase.biz.request.DoctorUserQuery;
import com.medcase.biz.request.DoctorUserReviewRequest;
import com.medcase.biz.response.DoctorUserVO;
import com.medcase.biz.service.DoctorUserService;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者管理。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/biz/patient-user")
public class PatientUserAdminController {
    private final DoctorUserService userService;

    @PreAuthorize("@ss.hasPermi('patient:user:list')")
    @GetMapping("/list")
    public PageResult<DoctorUserVO> list(
            PageParam pageParam, @NonNull DoctorUserQuery query) {
        return userService.page(pageParam, query, UserTypeEnums.PATIENT);
    }

    @PreAuthorize("@ss.hasPermi('patient:user:list')")
    @GetMapping("/{userId}")
    public DoctorUserVO getInfo(@PathVariable Long userId) {
        return userService.detail(userId, UserTypeEnums.PATIENT);
    }

    @PreAuthorize("@ss.hasPermi('patient:user:review')")
    @PostMapping("/{userId}/review")
    public void review(
            @PathVariable Long userId,
            @Valid @RequestBody DoctorUserReviewRequest request) {
        userService.review(userId, request, UserTypeEnums.PATIENT);
    }
}
