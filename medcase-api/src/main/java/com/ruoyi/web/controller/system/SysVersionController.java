package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.config.ProjectVersionService;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.web.controller.system.dto.VersionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统版本接口。
 *
 * @author medcase
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/system/version")
public class SysVersionController {
    private final ProjectVersionService projectVersionService;

    @Anonymous
    @GetMapping
    public R<VersionResponse> getVersion() {
        return R.ofSuccess(new VersionResponse(projectVersionService.getVersion()));
    }
}
