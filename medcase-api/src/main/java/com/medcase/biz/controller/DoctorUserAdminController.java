package com.medcase.biz.controller;

import com.medcase.biz.request.DoctorUserQuery;
import com.medcase.biz.request.DoctorUserReviewRequest;
import com.medcase.biz.response.DoctorUserVO;
import com.medcase.biz.service.DoctorUserService;
import jakarta.validation.Valid;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医生管理
 *
 * @author suyh
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/biz/doctor-user")
public class DoctorUserAdminController {
    private final DoctorUserService doctorUserService;

    @PreAuthorize("@ss.hasPermi('doctor:user:list')")
    @GetMapping("/list")
    public PageResult<DoctorUserVO> list(
            PageParam pageParam, @NonNull DoctorUserQuery query) {
        return doctorUserService.page(pageParam, query);
    }

    @PreAuthorize("@ss.hasPermi('doctor:user:list')")
    @GetMapping("/{userId}")
    public DoctorUserVO getInfo(@PathVariable Long userId) {
        return doctorUserService.detail(userId);
    }

    @PreAuthorize("@ss.hasPermi('doctor:user:list')")
    @PostMapping("/{userId}/review")
    public void review(
            @PathVariable Long userId,
            @Valid @RequestBody DoctorUserReviewRequest request) {
        doctorUserService.review(userId, request);
    }
}
