package com.medcase.web.controller.system;

import com.medcase.common.annotation.Anonymous;
import com.medcase.common.config.ProjectVersionService;
import com.medcase.mvc.response.R;
import com.medcase.web.controller.system.dto.VersionResponse;
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
