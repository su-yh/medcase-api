package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

/**
 * 岗位表实体。
 */
@Data
@TableName(value = "sys_post", autoResultMap = true)
public class SysPostEntity extends AbstractBaseEntity {

    @TableId(value = "post_id", type = IdType.AUTO)
    private Long postId;

    private String postCode;

    private String postName;

    private Integer postSort;

    private String status;

    private String createBy;

    private String updateBy;

    private String remark;
}
