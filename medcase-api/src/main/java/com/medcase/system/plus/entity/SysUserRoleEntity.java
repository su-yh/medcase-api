package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户和角色关联表实体。
 */
@Data
@TableName(value = "sys_user_role", autoResultMap = true)
public class SysUserRoleEntity {

    private Long userId;

    private Long roleId;
}
