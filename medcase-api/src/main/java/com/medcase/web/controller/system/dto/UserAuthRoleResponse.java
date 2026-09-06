package com.medcase.web.controller.system.dto;

import java.util.List;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.entity.SysRoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户授权角色响应数据。
 */
@Getter
@AllArgsConstructor
public class UserAuthRoleResponse {

    private SysUserEntity user;
    private List<SysRoleEntity> roles;
}
