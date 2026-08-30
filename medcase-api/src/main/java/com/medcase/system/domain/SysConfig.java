package com.medcase.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.medcase.common.core.domain.BaseEntity;
import lombok.Data;

/**
 * 参数配置表 sys_config
 * 
 */
@Data
public class SysConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 参数主键 */
    private Long configId;

    /** 参数名称 */
    @NotBlank(message = "参数名称不能为空")
    @Size(min = 0, max = 100, message = "参数名称不能超过100个字符")
    private String configName;

    /** 参数键名 */
    @NotBlank(message = "参数键名长度不能为空")
    @Size(min = 0, max = 100, message = "参数键名长度不能超过100个字符")
    private String configKey;

    /** 参数键值 */
    @NotBlank(message = "参数键值不能为空")
    @Size(min = 0, max = 500, message = "参数键值长度不能超过500个字符")
    private String configValue;

    /** 系统内置（Y是 N否） */
    private String configType;
}
