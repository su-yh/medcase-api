package com.medcase.web.controller.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 参数配置保存请求。
 */
@Data
public class ConfigSaveRequest {

    private Long configId;

    @NotBlank(message = "参数名称不能为空")
    @Size(max = 100, message = "参数名称不能超过100个字符")
    private String configName;

    @NotBlank(message = "参数键名不能为空")
    @Size(max = 100, message = "参数键名长度不能超过100个字符")
    private String configKey;

    @NotBlank(message = "参数键值不能为空")
    @Size(max = 500, message = "参数键值长度不能超过500个字符")
    private String configValue;

    private String configType;

    private String remark;
}
