package com.medcase.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

/**
 * 供应商实体。
 */
@Data
@TableName(value = "medcase_supplier", autoResultMap = true)
public class SupplierEntity extends AbstractBaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("nick_name")
    private String name;

    private String sex;

    private String phonenumber;

    private String email;

    private String idCardNumber;

    private String status;

    private String createBy;

    private String updateBy;

    private String remark;
}
