

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
    (2001, '病例管理', 2003, 300, 'case', null, '', '',
     1, 0, 'M', '0', '0', '', 'people', 'admin', sysdate(), '', null, '病例管理目录');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2002, '医生病例', 2001, 100, 'doctor', 'case/review/index', '', 'DoctorCase',
     1, 0, 'C', '0', '0', 'doctor:case:list', 'peoples', 'admin', sysdate(), '', null, '医生病例菜单');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2000, '医生管理', 2003, 100, 'doctor', 'biz/doctor/index', '', 'DoctorUser',
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
     1, 0, 'F', '0', '0', 'doctor:case:query', '#', 'admin', sysdate(), '', null, '医生病例查看按钮');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2006, '审核', 2002, 2, '', '', '', '',
     1, 0, 'F', '0', '0', 'doctor:case:review', '#', 'admin', sysdate(), '', null, '医生病例审核按钮');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2007, '结算', 2002, 3, '', '', '', '',
     1, 0, 'F', '0', '0', 'doctor:case:settle', '#', 'admin', sysdate(), '', null, '医生病例结算按钮');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2008, '患者管理', 2003, 200, 'patient', 'biz/patient/index', '', 'PatientUser',
     1, 0, 'C', '0', '0', 'patient:user:list', 'user', 'admin', sysdate(), '', null, '患者管理菜单');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2009, '审核', 2008, 1, '', '', '', '',
     1, 0, 'F', '0', '0', 'patient:user:review', '#', 'admin', sysdate(), '', null, '患者管理审核按钮');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2010, '患者病例', 2001, 200, 'patient', 'case/review/index', '', 'PatientCase',
     1, 0, 'C', '0', '0', 'patient:case:list', 'peoples', 'admin', sysdate(), '', null, '患者病例菜单');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2011, '查看', 2010, 1, '', '', '', '',
     1, 0, 'F', '0', '0', 'patient:case:query', '#', 'admin', sysdate(), '', null, '患者病例查看按钮');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2012, '审核', 2010, 2, '', '', '', '',
     1, 0, 'F', '0', '0', 'patient:case:review', '#', 'admin', sysdate(), '', null, '患者病例审核按钮');

insert into sys_menu
    (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
     is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
     create_time, update_by, update_time, remark)
values
    (2013, '结算', 2010, 3, '', '', '', '',
     1, 0, 'F', '0', '0', 'patient:case:settle', '#', 'admin', sysdate(), '', null, '患者病例结算按钮');
