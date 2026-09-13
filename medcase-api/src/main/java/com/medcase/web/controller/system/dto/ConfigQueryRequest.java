package com.medcase.web.controller.system.dto;

import com.medcase.common.enums.YesNoEnums;
import lombok.Data;

/**
 * 参数配置查询请求。
 */
@Data
public class ConfigQueryRequest {

    private String configName;

    private String configKey;

    private YesNoEnums configType;

    private String beginTime;

    private String endTime;
}
