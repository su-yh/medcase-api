package com.ruoyi.biz.doctor.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.biz.doctor.request.DoctorUserQuery;
import com.ruoyi.biz.doctor.response.DoctorUserVO;
import com.ruoyi.biz.doctor.service.DoctorUserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/biz/doctor")
public class DoctorUserController extends BaseController {
    private final DoctorUserService doctorUserService;

    @PreAuthorize("@ss.hasPermi('biz:doctor:list')")
    @GetMapping("/list")
    public TableDataInfo list(DoctorUserQuery query) {
        startPage();
        List<DoctorUserVO> list = doctorUserService.list(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('biz:doctor:query')")
    @GetMapping("/{userId}")
    public AjaxResult getInfo(@PathVariable Long userId) {
        DoctorUserVO doctor = doctorUserService.detail(userId);
        return doctor == null ? error("医生用户不存在") : success(doctor);
    }
}
