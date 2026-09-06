-- drop table if exists sys_audit_record;

CREATE TABLE sys_audit_record
(
    id            bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    trace_id      bigint       DEFAULT NULL COMMENT '业务操作ID',
    user_id       bigint       DEFAULT NULL COMMENT '用户ID',
    user_nickname varchar(64)  DEFAULT NULL COMMENT '用户昵称',
    operation     varchar(255) NULL DEFAULT NULL COMMENT '操作',
    req_method    varchar(20)  DEFAULT NULL COMMENT '请求方法',
    req_path      varchar(255) DEFAULT NULL COMMENT '请求路径',
    req_argument  text NULL DEFAULT NULL COMMENT '请求参数',
    result        text         DEFAULT NULL COMMENT '结果',
    result_code   varchar(128) DEFAULT NULL COMMENT '结果码',
    created       datetime NULL DEFAULT NULL COMMENT '创建日期',
    PRIMARY KEY (id)
) ENGINE = InnoDB COMMENT ='审计日志表';



