package com.medcase.web.controller.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色菜单关联修改请求。
 */
@Data
public class RoleMenuUpdateRequest {

    private boolean menuCheckStrictly;

    @NotNull(message = "菜单ID不能为空")
    private Long[] menuIds;
}
