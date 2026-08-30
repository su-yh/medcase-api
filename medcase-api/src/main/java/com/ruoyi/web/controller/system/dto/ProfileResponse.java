package com.ruoyi.web.controller.system.dto;

import com.ruoyi.common.core.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 个人资料响应数据。
 */
@Getter
@AllArgsConstructor
public class ProfileResponse {

    private SysUser data;
    private String roleGroup;
    private String postGroup;
}
