package com.medcase.web.controller.monitor;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Log;
import com.medcase.common.core.controller.BaseController;
import com.medcase.common.core.domain.R;
import com.medcase.common.core.page.TableDataInfo;
import com.medcase.common.enums.BusinessType;
import com.medcase.framework.web.service.SysPasswordService;
import com.medcase.system.domain.SysLogininfor;
import com.medcase.system.service.ISysLogininforService;

/**
 * 系统访问记录
 * 
 */
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {

    @Autowired
    private ISysLogininforService logininforService;

    @Autowired
    private SysPasswordService passwordService;

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysLogininfor logininfor) {

        startPage();
        List<SysLogininfor> list = logininforService.selectLogininforList(logininfor);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@PathVariable Long[] infoIds) {

        return logininforService.deleteLogininforByIds(infoIds) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:remove')")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {

        logininforService.cleanLogininfor();
        return R.ofSuccess();
    }

    @PreAuthorize("@ss.hasPermi('monitor:logininfor:unlock')")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public R<Void> unlock(@PathVariable("userName") String userName) {

        passwordService.clearLoginRecordCache(userName);
        return R.ofSuccess();
    }
}
