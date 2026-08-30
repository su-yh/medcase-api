package com.medcase.web.controller.system;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.common.annotation.Log;
import com.medcase.common.core.controller.BaseController;
import com.medcase.common.core.domain.R;
import com.medcase.common.core.page.TableDataInfo;
import com.medcase.common.enums.BusinessType;
import com.medcase.system.domain.SysConfig;
import com.medcase.system.service.ISysConfigService;

/**
 * 参数配置 信息操作处理
 * 
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController extends BaseController {

    @Autowired
    private ISysConfigService configService;

    /**
     * 获取参数配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysConfig config) {

        startPage();
        List<SysConfig> list = configService.selectConfigList(config);
        return getDataTable(list);
    }

    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public R<SysConfig> getInfo(@PathVariable Long configId) {

        return R.ofSuccess(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public R<String> getConfigKey(@PathVariable String configKey) {

        return R.ofSuccess(configService.selectConfigByKey(configKey));
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysConfig config) {

        if (!configService.checkConfigKeyUnique(config)) {

            return R.ofFail("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setCreateBy(getUsername());
        return configService.insertConfig(config) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysConfig config) {

        if (!configService.checkConfigKeyUnique(config)) {

            return R.ofFail("修改参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        config.setUpdateBy(getUsername());
        return configService.updateConfig(config) > 0 ? R.ofSuccess() : R.ofFail("操作失败");
    }

    /**
     * 删除参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public R<Void> remove(@PathVariable Long[] configIds) {

        configService.deleteConfigByIds(configIds);
        return R.ofSuccess();
    }

    /**
     * 刷新参数缓存
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R<Void> refreshCache() {

        configService.resetConfigCache();
        return R.ofSuccess();
    }
}
