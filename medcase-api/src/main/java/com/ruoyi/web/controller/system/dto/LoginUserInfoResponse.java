package com.ruoyi.web.controller.system.dto;

import com.ruoyi.common.core.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * 管理端当前用户信息响应。
 *
 * @author suyh
 */
@Getter
@AllArgsConstructor
public class LoginUserInfoResponse {
    private SysUser user;

    private Set<String> roles;

    private Set<String> permissions;

    private String pwdChrtype;

    private boolean defaultModifyPwd;

    private boolean passwordExpired;
}
