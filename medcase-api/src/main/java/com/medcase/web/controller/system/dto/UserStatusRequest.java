package com.medcase.web.controller.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户状态修改请求。
 */
@Data
public class UserStatusRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "用户状态不能为空")
    private String status;
}
