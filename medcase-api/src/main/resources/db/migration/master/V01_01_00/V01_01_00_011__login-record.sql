-- 新增统一登录记录表。
create table sys_login_record (
    id bigint not null auto_increment comment '主键',
    user_id bigint default null comment '用户ID',
    user_type varchar(2) default null comment '用户类型',
    user_name varchar(64) default null comment '用户账号',
    success tinyint default null comment '登录状态（1成功 0失败）',
    ipaddr varchar(128) default null comment '登录IP地址',
    login_location varchar(255) default null comment '登录地点',
    browser varchar(255) default null comment '浏览器',
    os varchar(255) default null comment '操作系统',
    msg varchar(255) default null comment '提示消息',
    login_time datetime default null comment '登录时间',
    primary key (id)
) engine=InnoDB comment='登录记录表';

-- 删除用户表中的最后登录信息。
alter table sys_user drop column login_ip;
alter table sys_user drop column login_date;

-- 删除旧登录记录表。
drop table if exists sys_logininfor;

-- 删除旧登录日志菜单及按钮权限。
delete from sys_role_menu where menu_id = 1042;
delete from sys_role_menu where menu_id = 1043;
delete from sys_role_menu where menu_id = 1045;
delete from sys_role_menu where menu_id = 501;
delete from sys_menu where id = 1042;
delete from sys_menu where id = 1043;
delete from sys_menu where id = 1045;
delete from sys_menu where id = 501;
