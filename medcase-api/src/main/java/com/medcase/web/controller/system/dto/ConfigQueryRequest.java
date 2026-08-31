package com.medcase.web.controller.system.dto;

import lombok.Data;

/**
 * 参数配置查询请求。
 */
@Data
public class ConfigQueryRequest {

    private String configName;

    private String configKey;

    private String configType;

    private String beginTime;

    private String endTime;
}
