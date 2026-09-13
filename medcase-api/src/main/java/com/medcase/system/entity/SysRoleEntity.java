package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;
import java.util.Set;

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

    private Boolean menuCheckStrictly;

    private Boolean enabled;

    @TableLogic
    @JsonIgnore
    private Boolean delFlag;

    private String remark;

    @TableField(exist = false)
    private boolean flag = false;

    @TableField(exist = false)
    private Set<String> permissions;
}
