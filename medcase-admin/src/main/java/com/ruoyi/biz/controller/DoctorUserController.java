package com.ruoyi.biz.controller;

import com.ruoyi.biz.request.DoctorUserQuery;
import com.ruoyi.biz.response.DoctorUserVO;
import com.ruoyi.biz.service.DoctorUserService;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class DoctorUserController {
    private final DoctorUserService doctorUserService;

    @PreAuthorize("@ss.hasPermi('biz:doctor:list')")
    @GetMapping("/list")
    public PageResult<DoctorUserVO> list(
            PageParam pageParam, @NonNull DoctorUserQuery query) {
        return doctorUserService.page(pageParam, query);
    }

    @PreAuthorize("@ss.hasPermi('biz:doctor:query')")
    @GetMapping("/{userId}")
    public DoctorUserVO getInfo(@PathVariable Long userId) {
        return doctorUserService.detail(userId);
    }
}
