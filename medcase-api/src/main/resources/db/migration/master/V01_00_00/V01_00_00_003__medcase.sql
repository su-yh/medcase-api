-- 病例表
create table medcase_case (
  id              bigint       not null auto_increment comment '主键ID',
  user_id         bigint       not null comment '用户ID',
  user_nickname   varchar(64)  not null comment '用户昵称',
  user_type       varchar(2)    not null comment '用户类型（01医生，02患者）',
  case_name       varchar(255) not null comment '病例名称',
  content         text         default null comment '病例内容',
  attachments     text         default null comment '病例附件JSON',
  status          varchar(64)  not null default 'pending_review' comment '病例状态编码',
  review_reason   varchar(500) default null comment '审核失败原因',
  reviewer_id     bigint       default null comment '审核人用户ID',
  reviewer_nickname varchar(64) default null comment '审核人昵称',
  settler_id      bigint       default null comment '结算人用户ID',
  settler_nickname varchar(64) default null comment '结算人昵称',
  submit_time     datetime(3)  default null comment '提交时间',
  review_time     datetime(3)  default null comment '审核时间',
  settled_time    datetime(3)  default null comment '结算时间',
  create_time     datetime(3)  not null comment '创建时间',
  update_time     datetime(3)  default null comment '更新时间',
  delete_flag     tinyint      not null default 0 comment '删除标志（0代表存在 1代表删除）',
  primary key (id)
) engine=innodb comment='病例表';

create index idx_medcase_case_user on medcase_case (user_id, user_type);
create index idx_medcase_case_status on medcase_case (status);
create index idx_medcase_case_submit_time on medcase_case (submit_time);
