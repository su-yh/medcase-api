package com.medcase.web.controller.system.dto;

import java.util.List;
import com.medcase.common.core.domain.TreeSelect;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 角色部门树响应数据。
 */
@Getter
@AllArgsConstructor
public class RoleDeptTreeResponse {

    private List<Long> checkedKeys;
    private List<TreeSelect> depts;
}
