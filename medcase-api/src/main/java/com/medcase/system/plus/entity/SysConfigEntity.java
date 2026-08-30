package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

/**
 * 参数配置表实体。
 */
@Data
@TableName(value = "sys_config", autoResultMap = true)
public class SysConfigEntity extends AbstractBaseEntity {

    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;

    private String configName;

    private String configKey;

    private String configValue;

    private String configType;

    private String createBy;

    private String updateBy;

    private String remark;
}
