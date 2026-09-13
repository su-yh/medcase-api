package com.medcase.web.controller.system.dto;

import com.medcase.common.enums.NormalDisableEnums;
import lombok.Data;

/**
 * 字典类型查询请求。
 */
@Data
public class DictTypeQueryRequest {

    private String dictName;

    private String dictType;

    private NormalDisableEnums status;

    private String beginTime;

    private String endTime;
}
