-- route_name 只保留菜单类型数据，目录和按钮不参与路由名称唯一性。
update sys_menu set route_name = null where menu_type <> 'C';

-- 历史菜单如果没有 route_name，先按旧 menu_id 生成临时唯一值，再创建唯一索引。
update sys_menu set route_name = concat('Menu', menu_id) where menu_type = 'C' and (route_name is null or route_name = '');

-- 兼容历史 visible：旧值 0=显示、1=隐藏；新值 1=显示、0=隐藏。
update sys_menu set visible = case visible when '0' then '1' when '1' then '0' else visible end where visible in ('0', '1');

alter table sys_menu rename column menu_id to id;
alter table sys_menu modify column id bigint not null auto_increment comment '主键';
alter table sys_menu rename column path to route_path;
alter table sys_menu modify column route_path varchar(200) default '' comment '路由路径';
alter table sys_menu rename column component to vue_component_path;
alter table sys_menu modify column vue_component_path varchar(255) default null comment '路由对应前端组件路径';
alter table sys_menu modify column route_name varchar(50) default null comment '路由名称，菜单必填且全局唯一';
alter table sys_menu modify column visible tinyint default 1 comment '菜单显示状态（1显示 0隐藏）';
create unique index uk_sys_menu_route_name on sys_menu(route_name);
