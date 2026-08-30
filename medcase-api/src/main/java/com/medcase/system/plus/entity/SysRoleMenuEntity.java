package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色和菜单关联表实体。
 */
@Data
@TableName(value = "sys_role_menu", autoResultMap = true)
public class SysRoleMenuEntity {

    private Long roleId;

    private Long menuId;
}
