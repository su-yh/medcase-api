package com.medcase.web.controller.system.dto;

import com.medcase.common.enums.NormalDisableEnums;
import lombok.Data;

/**
 * 岗位查询请求。
 */
@Data
public class PostQueryRequest {

    private String postCode;

    private String postName;

    private NormalDisableEnums status;
}
