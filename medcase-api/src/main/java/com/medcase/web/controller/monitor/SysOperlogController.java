package com.medcase.web.controller.monitor;

import java.util.List;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Log;
import com.medcase.common.core.controller.BaseController;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.enums.BusinessType;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.domain.SysOperLog;
import com.medcase.system.service.ISysOperLogService;

/**
 * 操作日志记录
 * 
 */
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController {

    @Autowired
    private ISysOperLogService operLogService;

    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public PageResult<SysOperLog> list(SysOperLog operLog) {

        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return new PageResult<>(list, new PageInfo<>(list).getTotal());
    }

    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public void remove(@PathVariable Long[] operIds) {

        if (operLogService.deleteOperLogByIds(operIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.OPERATION_FAILED);
        }
    }

    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public void clean() {

        operLogService.cleanOperLog();
    }
}
