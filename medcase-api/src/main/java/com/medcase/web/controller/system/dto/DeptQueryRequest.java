package com.medcase.web.controller.system.dto;

import com.medcase.common.enums.NormalDisableEnums;
import lombok.Data;

/**
 * 部门查询请求。
 */
@Data
public class DeptQueryRequest {

    private String deptNameLike;

    private NormalDisableEnums status;
}
