package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统访问记录表实体。
 */
@Data
@TableName(value = "sys_logininfor", autoResultMap = true)
public class SysLogininforEntity {

    @TableId(value = "info_id", type = IdType.AUTO)
    private Long infoId;

    private String userName;

    private String status;

    private String ipaddr;

    private String loginLocation;

    private String browser;

    private String os;

    private String msg;

    private Date loginTime;
}
