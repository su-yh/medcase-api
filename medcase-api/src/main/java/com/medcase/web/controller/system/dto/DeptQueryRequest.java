package com.medcase.web.controller.system.dto;

import lombok.Data;

/**
 * 部门查询请求。
 */
@Data
public class DeptQueryRequest {

    private String deptNameLike;

    private String status;
}
