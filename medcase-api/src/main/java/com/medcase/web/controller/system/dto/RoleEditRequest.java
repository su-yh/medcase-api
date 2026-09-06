package com.medcase.web.controller.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 角色修改请求。
 */
@Data
public class RoleEditRequest {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    private String roleName;

    @NotBlank(message = "权限字符不能为空")
    @Size(max = 100, message = "权限字符长度不能超过100个字符")
    private String roleKey;

    @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    private boolean menuCheckStrictly;

    private String status;

    private String remark;

}
