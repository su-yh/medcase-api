alter table sys_audit_record
    modify column trace_id varchar(32) default null comment '业务操作ID';

alter table sys_audit_record
    drop column if exists result_code;
