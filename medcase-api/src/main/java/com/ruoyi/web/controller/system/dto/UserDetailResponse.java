package com.ruoyi.web.controller.system.dto;

import java.util.List;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.SysPost;
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
    private List<SysRole> roles;
    private List<SysPost> posts;
}
