package com.medcase.web.controller.monitor;

import com.medcase.common.annotation.Log;
import com.medcase.common.enums.BusinessType;
import com.medcase.framework.web.service.SysPasswordService;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.entity.SysLogininforEntity;
import com.medcase.system.service.SysLogininforService;
import com.medcase.web.controller.monitor.dto.LogininforQueryRequest;
import com.medcase.web.controller.monitor.dto.LogininforResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统访问记录
 * 
 */
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController {

    @Autowired
    private SysLogininforService logininforService;

    @Autowired
    private SysPasswordService passwordService;

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public PageResult<LogininforResponse> list(PageParam pageParam, LogininforQueryRequest request) {

        PageResult<SysLogininforEntity> entityPage = logininforService.selectPage(
                pageParam, request.getIpaddr(), request.getStatus(), request.getUserName(),
                request.getBeginTime(), request.getEndTime());
        PageResult<LogininforResponse> result = new PageResult<>();
        result.setList(entityPage.getList().stream()
                .map(LogininforResponse::new)
                .toList());
        result.setTotal(entityPage.getTotal());
        return result;
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public void remove(@PathVariable Long[] infoIds) {

        if (logininforService.deleteLogininforByIds(infoIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.OPERATION_FAILED);
        }
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public void clean() {

        logininforService.cleanLogininfor();
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public void unlock(@PathVariable("userName") String userName) {

        passwordService.clearLoginRecordCache(userName);
    }
}
