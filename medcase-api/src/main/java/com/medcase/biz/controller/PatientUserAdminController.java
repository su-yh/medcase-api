package com.medcase.biz.controller;

import com.medcase.biz.request.UserQuery;
import com.medcase.biz.request.UserReviewRequest;
import com.medcase.biz.response.UserVO;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.service.SysUserService;
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
    private final SysUserService userService;

    @PreAuthorize("@ss.hasPermi('patient:user:list')")
    @GetMapping("/list")
    public PageResult<UserVO> list(
            PageParam pageParam, @NonNull UserQuery query) {
        PageResult<SysUserEntity> page = userService.selectBizPage(
                pageParam, query, UserTypeEnums.PATIENT);
        PageResult<UserVO> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setList(page.getList().stream().map(UserVO::fromEntity).toList());
        return result;
    }

    @PreAuthorize("@ss.hasPermi('patient:user:list')")
    @GetMapping("/{userId}")
    public UserVO getInfo(@PathVariable Long userId) {
        SysUserEntity user = userService.selectBizUserById(userId, UserTypeEnums.PATIENT);
        return user == null ? null : UserVO.fromEntity(user);
    }

    @PreAuthorize("@ss.hasPermi('patient:user:review')")
    @PostMapping("/{userId}/review")
    public void review(
            @PathVariable Long userId,
            @Valid @RequestBody UserReviewRequest request) {
        userService.reviewUser(userId, request, UserTypeEnums.PATIENT);
    }
}
