

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

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2004, '审核', 2000, 1, '', '', '', '',
     1, 0, 'F', '0', '0', 'biz:doctor:review', '#', 'admin', sysdate(), '', null, '医生管理审核按钮');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2005, '查看', 2002, 1, '', '', '', '',
     1, 0, 'F', '0', '0', 'case:review:query', '#', 'admin', sysdate(), '', null, '病例审核查看按钮');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2006, '审核', 2002, 2, '', '', '', '',
     1, 0, 'F', '0', '0', 'case:review:review', '#', 'admin', sysdate(), '', null, '病例审核审核按钮');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2007, '结算', 2002, 3, '', '', '', '',
     1, 0, 'F', '0', '0', 'case:review:settle', '#', 'admin', sysdate(), '', null, '病例审核结算按钮');
