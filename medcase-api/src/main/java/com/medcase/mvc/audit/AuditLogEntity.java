package com.medcase.mvc.audit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author suyh
 * @since 2024-10-18
 */
@Data
@TableName(value = "operation_record", autoResultMap = true)
public class AuditLogEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("trace_id")
    private Long traceId;

    @TableField("user_id")
    private Long userId;

    @TableField("user_nickname")
    private String userNickname;

    @TableField("page")
    private String page;

    @TableField("operation")
    private String operation;

    @TableField("req_argument")
    private String reqArgument;

    @TableField("result")
    private String result;

    @TableField("req_path")
    private String reqPath;

    @TableField("req_method")
    private String reqMethod;

    @TableField(value = "sql_list", typeHandler = JacksonTypeHandler.class)
    private List<String> sqlList;

    @TableField("created")
    private Date created;
}
