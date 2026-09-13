package com.medcase.web.controller.system.dto;

import lombok.Data;

/**
 * 字典类型查询请求。
 */
@Data
public class DictTypeQueryRequest {

    private String dictName;

    private String dictType;

    private Boolean enabled;

    private String beginTime;

    private String endTime;
}
