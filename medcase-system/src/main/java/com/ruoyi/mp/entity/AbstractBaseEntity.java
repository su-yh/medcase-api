package com.ruoyi.mp.entity;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

/**
 * @author suyh
 * @since 2026-08-22
 */
@Data
@FieldNameConstants
public abstract class AbstractBaseEntity {

    private Date createTime;

    private Date updateTime;
}
