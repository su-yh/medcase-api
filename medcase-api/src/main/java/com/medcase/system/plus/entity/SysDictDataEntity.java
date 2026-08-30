package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

/**
 * 字典数据表实体。
 */
@Data
@TableName(value = "sys_dict_data", autoResultMap = true)
public class SysDictDataEntity extends AbstractBaseEntity {

    @TableId(value = "dict_code", type = IdType.AUTO)
    private Long dictCode;

    private Integer dictSort;

    private String dictLabel;

    private String dictValue;

    private String dictType;

    private String cssClass;

    private String listClass;

    private String isDefault;

    private String status;

    private String createBy;

    private String updateBy;

    private String remark;
}
