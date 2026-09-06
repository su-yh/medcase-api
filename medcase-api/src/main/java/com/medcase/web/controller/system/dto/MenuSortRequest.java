package com.medcase.web.controller.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 菜单排序请求。
 */
@Data
public class MenuSortRequest {

    @NotBlank(message = "菜单ID不能为空")
    private String menuIds;

    @NotBlank(message = "菜单排序不能为空")
    private String orderNums;
}
