package com.medcase.mvc.audit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author suyh
 * @since 2024-10-18
 */
@Data
@TableName(value = "sys_audit_record", autoResultMap = true)
public class AuditLogEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("trace_id")
    private Long traceId;

    @TableField("user_id")
    private Long userId;

    @TableField("user_nickname")
    private String userNickname;

    @TableField("operation")
    private AuditEnums operation;

    @TableField("req_argument")
    private String reqArgument;

    @TableField("result_detail")
    private String resultDetail;

    @TableField("result_code")
    private String resultCode;

    @TableField("req_path")
    private String reqPath;

    @TableField("req_method")
    private String reqMethod;

    @TableField("created")
    private Date created;
}
