package com.medcase.web.controller.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medcase.system.entity.SysConfigEntity;
import lombok.Data;

import java.util.Date;

/**
 * 参数配置响应。
 */
@Data
public class ConfigResponse {

    private Long configId;

    private String configName;

    private String configKey;

    private String configValue;

    private String configType;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public static ConfigResponse fromEntity(SysConfigEntity entity) {
        if (entity == null) {
            return null;
        }
        ConfigResponse response = new ConfigResponse();
        response.setConfigId(entity.getConfigId());
        response.setConfigName(entity.getConfigName());
        response.setConfigKey(entity.getConfigKey());
        response.setConfigValue(entity.getConfigValue());
        response.setConfigType(entity.getConfigType());
        response.setRemark(entity.getRemark());
        response.setCreateTime(entity.getCreateTime());
        return response;
    }
}
