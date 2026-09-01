package com.medcase.web.controller.system;

import com.medcase.common.annotation.Log;
import com.medcase.common.core.controller.BaseController;
import com.medcase.common.enums.BusinessType;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysConfigEntity;
import com.medcase.system.service.ISysConfigService;
import com.medcase.web.controller.system.dto.ConfigQueryRequest;
import com.medcase.web.controller.system.dto.ConfigResponse;
import com.medcase.web.controller.system.dto.ConfigSaveRequest;
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
    public PageResult<ConfigResponse> list(PageParam pageParam, ConfigQueryRequest request) {

        PageResult<SysConfigEntity> entityPage = configService.selectPage(
                pageParam, request.getConfigName(), request.getConfigType(), request.getConfigKey(),
                request.getBeginTime(), request.getEndTime());
        PageResult<ConfigResponse> result = new PageResult<>();
        result.setList(entityPage.getList().stream()
                .map(ConfigResponse::fromEntity)
                .toList());
        result.setTotal(entityPage.getTotal());
        return result;
    }

    /**
     * 根据参数编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping(value = "/{configId}")
    public ConfigResponse getInfo(@PathVariable Long configId) {

        return ConfigResponse.fromEntity(configService.selectConfigById(configId));
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public String getConfigKey(@PathVariable String configKey) {

        return configService.selectConfigByKey(configKey);
    }

    /**
     * 新增参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @Log(title = "参数管理", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@Validated @RequestBody ConfigSaveRequest request) {

        if (!configService.checkConfigKeyUnique(request.getConfigId(), request.getConfigKey())) {
            throw ExceptionUtil.business(ErrorCodeEnums.CONFIG_KEY_EXISTS);
        }
        SysConfigEntity config = toEntity(request);
        config.setCreateBy(getUsername());
        if (configService.insertConfig(config) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.CONFIG_OPERATION_FAILED);
        }
    }

    /**
     * 修改参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @Log(title = "参数管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public void edit(@Validated @RequestBody ConfigSaveRequest request) {

        if (!configService.checkConfigKeyUnique(request.getConfigId(), request.getConfigKey())) {
            throw ExceptionUtil.business(ErrorCodeEnums.CONFIG_KEY_EXISTS);
        }
        SysConfigEntity config = toEntity(request);
        config.setUpdateBy(getUsername());
        if (configService.updateConfig(config) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.CONFIG_OPERATION_FAILED);
        }
    }

    /**
     * 删除参数配置
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public void remove(@PathVariable Long[] configIds) {

        configService.deleteConfigByIds(configIds);
    }

    /**
     * 刷新参数缓存
     */
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public void refreshCache() {

        configService.resetConfigCache();
    }

    private SysConfigEntity toEntity(ConfigSaveRequest request) {
        SysConfigEntity entity = new SysConfigEntity();
        entity.setConfigId(request.getConfigId());
        entity.setConfigName(request.getConfigName());
        entity.setConfigKey(request.getConfigKey());
        entity.setConfigValue(request.getConfigValue());
        entity.setConfigType(request.getConfigType());
        entity.setRemark(request.getRemark());
        return entity;
    }
}
