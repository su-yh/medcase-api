package com.medcase.web.controller.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Log;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.enums.BusinessType;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysOperLogEntity;
import com.medcase.system.service.SysOperLogService;
import com.medcase.web.controller.monitor.dto.OperLogQueryRequest;
import com.medcase.web.controller.monitor.dto.OperLogResponse;

/**
 * 操作日志记录
 * 
 */
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController {

    @Autowired
    private SysOperLogService operLogService;

    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public PageResult<OperLogResponse> list(PageParam pageParam, OperLogQueryRequest request) {

        PageResult<SysOperLogEntity> entityPage = operLogService.selectPage(
                pageParam, request.getOperIp(), request.getTitle(), request.getBusinessType(),
                request.getStatus(), request.getOperName(),
                request.getBeginTime(), request.getEndTime());
        PageResult<OperLogResponse> result = new PageResult<>();
        result.setList(entityPage.getList().stream()
                .map(OperLogResponse::new)
                .toList());
        result.setTotal(entityPage.getTotal());
        return result;
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
