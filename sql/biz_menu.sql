-- ----------------------------
-- 业务菜单初始化
-- 说明：
-- 1. 本文件依赖 sql/ry_20260417.sql 已创建 sys_menu 表。
-- 2. 使用固定菜单 ID，保证各环境的父子菜单关系一致。
-- 3. 不初始化 sys_role_menu，角色授权由管理端角色管理完成。
-- ----------------------------

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2003, '业务管理', 0, 100, 'biz', null, '', '',
     1, 0, 'M', '0', '0', '', 'build', 'admin', sysdate(), '', null, '业务管理目录');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2001, '病例管理', 2003, 101, 'case', null, '', '',
     1, 0, 'M', '0', '0', '', 'people', 'admin', sysdate(), '', null, '病例管理目录');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2002, '病例审核', 2001, 100, 'review', 'case/review/index', '', '',
     1, 0, 'C', '0', '0', 'case:review:list', 'peoples', 'admin', sysdate(), '', null, '病例审核菜单');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2000, '医生管理', 2003, 100, 'doctor', 'biz/doctor/index', '', '',
     1, 0, 'C', '0', '0', 'doctor:user:list', 'user', 'admin', sysdate(), '', null, '医生管理菜单');
