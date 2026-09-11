package com.medcase.web.controller.monitor;

import jakarta.servlet.http.HttpServletRequest;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.framework.web.service.SysPasswordService;
import com.medcase.mvc.audit.AuditOperation;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
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
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).DELETE_LOGIN_LOG, " +
            "#spelReturnValue, #servletRequest, #loginUser, #infoIds)")
    @DeleteMapping("/{infoIds}")
    public void remove(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @PathVariable Long[] infoIds) {
        if (logininforService.deleteLogininforByIds(infoIds) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.OPERATION_FAILED);
        }
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).CLEAN_LOGIN_LOG, " +
            "#spelReturnValue, #servletRequest, #loginUser)")
    @DeleteMapping("/clean")
    public void clean(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser) {
        logininforService.cleanLogininfor();
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')")
    @AuditOperation("@audit.auditRecord(" +
            "T(com.medcase.mvc.audit.AuditEnums).UNLOCK_LOGIN_USER, " +
            "#spelReturnValue, #servletRequest, #loginUser, #userName)")
    @GetMapping("/unlock/{userName}")
    public void unlock(
            HttpServletRequest servletRequest,
            @CurrLoginUser LoginUser loginUser,
            @PathVariable("userName") String userName) {
        passwordService.clearLoginRecordCache(userName);
    }
}
