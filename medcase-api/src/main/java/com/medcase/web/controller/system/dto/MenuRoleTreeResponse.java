package com.medcase.web.controller.system.dto;

import java.util.List;
import com.medcase.common.core.domain.TreeSelect;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色菜单树响应数据。
 */
@Getter
@AllArgsConstructor
public class MenuRoleTreeResponse {

    private List<Long> checkedKeys;
    private List<TreeSelect> menus;
}
