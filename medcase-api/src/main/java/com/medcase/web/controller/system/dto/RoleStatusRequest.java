package com.medcase.web.controller.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色状态修改请求。
 */
@Data
public class RoleStatusRequest {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    private String status;
}
