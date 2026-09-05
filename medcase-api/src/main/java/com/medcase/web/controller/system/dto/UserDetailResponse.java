package com.medcase.web.controller.system.dto;

import java.util.List;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.system.entity.SysRoleEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户详情响应数据。
 */
@Getter
@AllArgsConstructor
public class UserDetailResponse {

    private SysUser data;
    private List<Long> postIds;
    private List<Long> roleIds;
    private List<SysRoleEntity> roles;
    private List<PostResponse> posts;
}
