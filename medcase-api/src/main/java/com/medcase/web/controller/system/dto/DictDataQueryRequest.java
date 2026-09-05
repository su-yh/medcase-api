package com.medcase.web.controller.system.dto;

import lombok.Data;

/**
 * 字典数据查询请求。
 */
@Data
public class DictDataQueryRequest {

    private String dictType;

    private String dictLabel;

    private String status;
}
