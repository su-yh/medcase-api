package com.medcase.web.controller.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 部门排序请求。
 */
@Data
public class DeptSortRequest {

    @NotBlank(message = "部门ID不能为空")
    private String deptIds;

    @NotBlank(message = "部门排序不能为空")
    private String orderNums;
}
