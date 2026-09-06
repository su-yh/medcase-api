package com.medcase.web.controller.system.dto;

import com.medcase.system.entity.SysUserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 个人资料响应数据。
 */
@Getter
@AllArgsConstructor
public class ProfileResponse {

    private SysUserEntity data;
    private String roleGroup;
    private String postGroup;
}
