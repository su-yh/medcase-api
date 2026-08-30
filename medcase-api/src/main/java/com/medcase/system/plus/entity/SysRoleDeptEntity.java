package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色和部门关联表实体。
 */
@Data
@TableName(value = "sys_role_dept", autoResultMap = true)
public class SysRoleDeptEntity {

    private Long roleId;

    private Long deptId;
}
