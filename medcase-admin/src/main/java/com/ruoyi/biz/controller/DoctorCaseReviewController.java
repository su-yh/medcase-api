package com.ruoyi.biz.controller;

import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.biz.request.DoctorCaseReviewQuery;
import com.ruoyi.biz.response.DoctorCaseReviewVO;
import com.ruoyi.biz.service.DoctorCaseReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class DoctorCaseReviewController {
    private final DoctorCaseReviewService doctorCaseReviewService;

    @PreAuthorize("@ss.hasPermi('biz:case:review:list')")
    @GetMapping("/list")
    public PageResult<DoctorCaseReviewVO> list(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            DoctorCaseReviewQuery query) {
        return doctorCaseReviewService.page(pageNum, pageSize, query);
    }

    @PreAuthorize("@ss.hasPermi('biz:case:review:query')")
    @GetMapping("/{id}")
    public DoctorCaseReviewVO getInfo(@PathVariable Long id) {
        return doctorCaseReviewService.detail(id);
    }
}
