package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

/**
 * 角色表实体。
 */
@Data
@TableName(value = "sys_role", autoResultMap = true)
public class SysRoleEntity extends AbstractBaseEntity {

    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    private String roleName;

    private String roleKey;

    private Integer roleSort;

    private String dataScope;

    private Boolean menuCheckStrictly;

    private Boolean deptCheckStrictly;

    private String status;

    private String delFlag;

    private String createBy;

    private String updateBy;

    private String remark;
}
