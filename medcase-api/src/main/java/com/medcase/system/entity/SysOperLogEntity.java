package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 操作日志记录表实体。
 */
@Data
@TableName(value = "sys_oper_log", autoResultMap = true)
public class SysOperLogEntity {

    @TableId(value = "oper_id", type = IdType.AUTO)
    private Long operId;

    private String title;

    private Integer businessType;

    private String method;

    private String requestMethod;

    private Integer operatorType;

    private String operName;

    private String deptName;

    private String operUrl;

    private String operIp;

    private String operLocation;

    private String operParam;

    private String jsonResult;

    private Integer status;

    private String errorMsg;

    private Date operTime;

    private Long costTime;
}
