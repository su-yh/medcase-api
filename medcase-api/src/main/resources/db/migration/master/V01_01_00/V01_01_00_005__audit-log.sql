-- drop table if exists sys_audit_record;

create table sys_audit_record (
    id bigint unsigned not null auto_increment comment '主键',
    trace_id varchar(32) default null comment '业务操作ID',
    user_id bigint default null comment '用户ID',
    user_nickname varchar(64) default null comment '用户昵称',
    operation varchar(255) default null comment '操作',
    req_method varchar(20) default null comment '请求方法',
    req_path varchar(255) default null comment '请求路径',
    req_argument text default null comment '请求参数',
    result_detail text default null comment '结果明细',
    created datetime default null comment '创建日期',
    primary key (id)
) engine=InnoDB comment='审计日志表';
