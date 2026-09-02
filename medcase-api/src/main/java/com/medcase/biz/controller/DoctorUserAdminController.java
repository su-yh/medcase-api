package com.medcase.biz.controller;

import com.medcase.biz.request.UserQuery;
import com.medcase.biz.request.UserReviewRequest;
import com.medcase.biz.response.UserVO;
import com.medcase.biz.service.UserService;
import com.medcase.common.enums.UserTypeEnums;
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
    private final UserService userService;

    @PreAuthorize("@ss.hasPermi('doctor:user:list')")
    @GetMapping("/list")
    public PageResult<UserVO> list(
            PageParam pageParam, @NonNull UserQuery query) {
        return userService.page(pageParam, query, UserTypeEnums.DOCTOR);
    }

    @PreAuthorize("@ss.hasPermi('doctor:user:list')")
    @GetMapping("/{userId}")
    public UserVO getInfo(@PathVariable Long userId) {
        return userService.detail(userId);
    }

    @PreAuthorize("@ss.hasPermi('doctor:user:list')")
    @PostMapping("/{userId}/review")
    public void review(
            @PathVariable Long userId,
            @Valid @RequestBody UserReviewRequest request) {
        userService.review(userId, request);
    }
}
