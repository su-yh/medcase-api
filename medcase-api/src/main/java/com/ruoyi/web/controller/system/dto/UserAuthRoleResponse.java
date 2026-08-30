package com.ruoyi.web.controller.system.dto;

import java.util.List;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户授权角色响应数据。
 */
@Getter
@AllArgsConstructor
public class UserAuthRoleResponse {

    private SysUser user;
    private List<SysRole> roles;
}
