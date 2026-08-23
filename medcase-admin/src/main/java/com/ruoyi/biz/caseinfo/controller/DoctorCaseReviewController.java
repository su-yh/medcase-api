package com.ruoyi.biz.caseinfo.controller;

import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.biz.caseinfo.request.DoctorCaseReviewQuery;
import com.ruoyi.biz.caseinfo.response.DoctorCaseReviewVO;
import com.ruoyi.biz.caseinfo.service.DoctorCaseReviewService;
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
@RequestMapping("/biz/case/review")
public class DoctorCaseReviewController extends BaseController {
    private final DoctorCaseReviewService doctorCaseReviewService;

    @PreAuthorize("@ss.hasPermi('biz:case:review:list')")
    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize,
            DoctorCaseReviewQuery query) {
        PageResult<DoctorCaseReviewVO> pageResult =
                doctorCaseReviewService.page(pageNum, pageSize, query);
        TableDataInfo result = new TableDataInfo();
        result.setCode(HttpStatus.SUCCESS);
        result.setMsg("查询成功");
        result.setRows(pageResult.getList());
        result.setTotal(pageResult.getTotal());
        return result;
    }

    @PreAuthorize("@ss.hasPermi('biz:case:review:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        DoctorCaseReviewVO review = doctorCaseReviewService.detail(id);
        return review == null ? error("病例不存在") : success(review);
    }
}
