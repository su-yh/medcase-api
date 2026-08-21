-- 医生端病例表
create table medcase_doctor_case (
  id              bigint       not null auto_increment comment '主键ID',
  doctor_id       bigint       not null comment '医生用户ID',
  doctor_nickname varchar(64)  not null comment '医生昵称',
  title           varchar(255) not null comment '病例标题',
  remark          varchar(500) default null comment '病例备注',
  attachments     text         default null comment '病例附件JSON',
  status          varchar(64)  not null default 'pending_review' comment '病例状态编码',
  review_reason   varchar(500) default null comment '审核失败原因',
  review_time     datetime(3)  default null comment '审核时间',
  settled_time    datetime(3)  default null comment '结算时间',
  create_time     datetime(3)  not null comment '提交时间',
  update_time     datetime(3)  default null comment '更新时间',
  delete_flag     tinyint      not null default 0 comment '删除标志（0代表存在 1代表删除）',
  primary key (id)
) engine=innodb comment='医生病例表';

create index idx_medcase_doctor_case_doctor_id on medcase_doctor_case (doctor_id);
create index idx_medcase_doctor_case_status on medcase_doctor_case (status);
create index idx_medcase_doctor_case_create_time on medcase_doctor_case (create_time);
