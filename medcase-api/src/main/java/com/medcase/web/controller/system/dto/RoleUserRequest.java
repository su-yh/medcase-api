package com.medcase.web.controller.system.dto;

import lombok.Data;

/**
 * 角色授权用户关联请求。
 */
@Data
public class RoleUserRequest {

    private Long userId;

    private Long roleId;
}
