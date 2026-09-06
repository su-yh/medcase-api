alter table sys_dept add column create_user_id bigint default null comment '创建者用户ID';
alter table sys_dept add column update_user_id bigint default null comment '更新者用户ID';

alter table sys_user add column create_user_id bigint default null comment '创建者用户ID';
alter table sys_user add column update_user_id bigint default null comment '更新者用户ID';

alter table sys_post add column create_user_id bigint default null comment '创建者用户ID';
alter table sys_post add column update_user_id bigint default null comment '更新者用户ID';

alter table sys_role add column create_user_id bigint default null comment '创建者用户ID';
alter table sys_role add column update_user_id bigint default null comment '更新者用户ID';

alter table sys_menu add column create_user_id bigint default null comment '创建者用户ID';
alter table sys_menu add column update_user_id bigint default null comment '更新者用户ID';

alter table sys_dict_type add column create_user_id bigint default null comment '创建者用户ID';
alter table sys_dict_type add column update_user_id bigint default null comment '更新者用户ID';

alter table sys_dict_data add column create_user_id bigint default null comment '创建者用户ID';
alter table sys_dict_data add column update_user_id bigint default null comment '更新者用户ID';

alter table sys_config add column create_user_id bigint default null comment '创建者用户ID';
alter table sys_config add column update_user_id bigint default null comment '更新者用户ID';

alter table sys_notice add column create_user_id bigint default null comment '创建者用户ID';
alter table sys_notice add column update_user_id bigint default null comment '更新者用户ID';

alter table medcase_supplier add column create_user_id bigint default null comment '创建者用户ID';
alter table medcase_supplier add column update_user_id bigint default null comment '更新者用户ID';

alter table medcase_case add column create_by varchar(64) default null comment '创建者';
alter table medcase_case add column create_user_id bigint default null comment '创建者用户ID';
alter table medcase_case add column update_by varchar(64) default null comment '更新者';
alter table medcase_case add column update_user_id bigint default null comment '更新者用户ID';
