package com.medcase.biz.controller;

import com.medcase.biz.request.UserQuery;
import com.medcase.biz.request.UserReviewRequest;
import com.medcase.biz.response.UserVO;
import com.medcase.biz.service.UserService;
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
    private final UserService userService;

    @PreAuthorize("@ss.hasPermi('patient:user:list')")
    @GetMapping("/list")
    public PageResult<UserVO> list(
            PageParam pageParam, @NonNull UserQuery query) {
        return userService.page(pageParam, query, UserTypeEnums.PATIENT);
    }

    @PreAuthorize("@ss.hasPermi('patient:user:list')")
    @GetMapping("/{userId}")
    public UserVO getInfo(@PathVariable Long userId) {
        return userService.detail(userId, UserTypeEnums.PATIENT);
    }

    @PreAuthorize("@ss.hasPermi('patient:user:review')")
    @PostMapping("/{userId}/review")
    public void review(
            @PathVariable Long userId,
            @Valid @RequestBody UserReviewRequest request) {
        userService.review(userId, request, UserTypeEnums.PATIENT);
    }
}
