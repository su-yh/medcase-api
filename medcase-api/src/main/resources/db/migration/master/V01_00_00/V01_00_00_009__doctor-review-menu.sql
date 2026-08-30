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



