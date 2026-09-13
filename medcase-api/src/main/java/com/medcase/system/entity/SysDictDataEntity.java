package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.common.enums.NormalDisableEnums;
import com.medcase.common.enums.YesNoEnums;
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

    private Long dictSort;

    private String dictLabel;

    private String dictValue;

    private String dictType;

    private String cssClass;

    private String listClass;

    private YesNoEnums isDefault;

    private NormalDisableEnums status;

    private String remark;
}
