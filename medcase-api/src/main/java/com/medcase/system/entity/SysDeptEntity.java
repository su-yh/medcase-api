package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

/**
 * 部门表实体。
 */
@Data
@TableName(value = "sys_dept", autoResultMap = true)
public class SysDeptEntity extends AbstractBaseEntity {

    @TableId(value = "dept_id", type = IdType.AUTO)
    private Long deptId;

    private Long parentId;

    private String ancestors;

    private String deptName;

    private Integer orderNum;

    private String leader;

    private String phone;

    private String email;

    private String status;

    private String delFlag;

    private String createBy;

    private String updateBy;
}
