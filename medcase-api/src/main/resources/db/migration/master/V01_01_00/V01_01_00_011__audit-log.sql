-- drop table if exists operation_record;

CREATE TABLE operation_record
(
    id            bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    trace_id      bigint               DEFAULT NULL COMMENT '业务操作ID',
    user_id       bigint               DEFAULT NULL COMMENT '用户ID',
    user_nickname varchar(64)          DEFAULT NULL COMMENT '用户昵称',
    page          varchar(255)         DEFAULT NULL COMMENT '页面',
    operation     varchar(255)    NULL DEFAULT NULL COMMENT '操作',
    req_method    varchar(20)          DEFAULT NULL COMMENT '请求方法',
    req_path      varchar(255)         DEFAULT NULL COMMENT '请求路径',
    req_argument  text            NULL DEFAULT NULL COMMENT '请求参数',
    result        text                 DEFAULT NULL COMMENT '结果',
    sql_list      text                 default NULL COMMENT '操作SQL列表，以json 格式存储',
    created       datetime        NULL DEFAULT NULL COMMENT '创建日期',
    PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT ='审计日志表';



