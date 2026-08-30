package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

/**
 * 字典类型表实体。
 */
@Data
@TableName(value = "sys_dict_type", autoResultMap = true)
public class SysDictTypeEntity extends AbstractBaseEntity {

    @TableId(value = "dict_id", type = IdType.AUTO)
    private Long dictId;

    private String dictName;

    private String dictType;

    private String status;

    private String createBy;

    private String updateBy;

    private String remark;
}
