-- 供应商关联
-- 供应商表
create table medcase_supplier (
    id              bigint       not null auto_increment comment '供应商ID',
    nick_name       varchar(30)  not null comment '供应商昵称',
    sex             char(1)      not null comment '性别（0男 1女 2未知）',
    phonenumber     varchar(20)  not null comment '手机号码',
    email           varchar(50)  default null comment '邮箱',
    id_card_number  varchar(30)  not null comment '身份证号',
    status          char(1)      not null default '0' comment '状态（0正常 1停用）',
    create_by       varchar(64)  not null default '' comment '创建者',
    create_time     datetime     not null comment '创建时间',
    update_by       varchar(64)  not null default '' comment '更新者',
    update_time     datetime     default null comment '更新时间',
    remark          varchar(500) default null comment '备注',
    primary key (id),
    unique key uk_medcase_supplier_nick_name (nick_name)
) engine=innodb auto_increment=100 comment='供应商表';

-- 业务管理-供应商管理
insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2014, '供应商管理', 2003, 300, 'supplier', 'biz/supplier/index', '', 'Supplier',
     1, 0, 'C', '0', '0', 'supplier:list', 'user', 'admin', sysdate(), '', null, '供应商管理菜单');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2015, '查询', 2014, 1, '', '', '', '',
     1, 0, 'F', '0', '0', 'supplier:query', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2016, '新增', 2014, 2, '', '', '', '',
     1, 0, 'F', '0', '0', 'supplier:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2017, '修改', 2014, 3, '', '', '', '',
     1, 0, 'F', '0', '0', 'supplier:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2018, '状态', 2014, 4, '', '', '', '',
     1, 0, 'F', '0', '0', 'supplier:status', '#', 'admin', sysdate(), '', null, '');
