-- 业务初始化数据基线。
-- 该文件只用于首次上线数据库，依赖 001 系统表结构和 002 业务表结构。

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2003, '业务管理', 0, 100, 'biz', null, null, 'M', 1, 1, '', 'build', null, 'admin', sysdate(), null, '', null, '业务管理目录');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2001, '病例管理', 2003, 400, 'case', null, null, 'M', 1, 1, '', 'people', null, 'admin', sysdate(), null, '', null, '病例管理目录');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2002, '医生病例', 2001, 100, 'doctor', 'case/review/index', 'BizCaseDoctor', 'C', 1, 1, 'doctor:case:list', 'peoples', null, 'admin', sysdate(), null, '', null, '医生病例菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2000, '医生管理', 2003, 100, 'doctor', 'biz/doctor/index', 'BizDoctor', 'C', 1, 1, 'doctor:user:list', 'user', null, 'admin', sysdate(), null, '', null, '医生管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2004, '审核', 2000, 1, '', '', null, 'F', 1, 1, 'biz:doctor:review', '#', null, 'admin', sysdate(), null, '', null, '医生管理审核按钮');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2005, '查看', 2002, 1, '', '', null, 'F', 1, 1, 'doctor:case:query', '#', null, 'admin', sysdate(), null, '', null, '医生病例查看按钮');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2006, '审核', 2002, 2, '', '', null, 'F', 1, 1, 'doctor:case:review', '#', null, 'admin', sysdate(), null, '', null, '医生病例审核按钮');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2007, '结算', 2002, 3, '', '', null, 'F', 1, 1, 'doctor:case:settle', '#', null, 'admin', sysdate(), null, '', null, '医生病例结算按钮');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2008, '患者管理', 2003, 200, 'patient', 'biz/patient/index', 'BizPatient', 'C', 1, 1, 'patient:user:list', 'user', null, 'admin', sysdate(), null, '', null, '患者管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2009, '审核', 2008, 1, '', '', null, 'F', 1, 1, 'patient:user:review', '#', null, 'admin', sysdate(), null, '', null, '患者管理审核按钮');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2010, '患者病例', 2001, 200, 'patient', 'case/review/index', 'BizCasePatient', 'C', 1, 1, 'patient:case:list', 'peoples', null, 'admin', sysdate(), null, '', null, '患者病例菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2011, '查看', 2010, 1, '', '', null, 'F', 1, 1, 'patient:case:query', '#', null, 'admin', sysdate(), null, '', null, '患者病例查看按钮');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2012, '审核', 2010, 2, '', '', null, 'F', 1, 1, 'patient:case:review', '#', null, 'admin', sysdate(), null, '', null, '患者病例审核按钮');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2013, '结算', 2010, 3, '', '', null, 'F', 1, 1, 'patient:case:settle', '#', null, 'admin', sysdate(), null, '', null, '患者病例结算按钮');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2014, '供应商管理', 2003, 300, 'supplier', 'biz/supplier/index', 'Supplier', 'C', 1, 1, 'supplier:list', 'user', null, 'admin', sysdate(), null, '', null, '供应商管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2015, '查询', 2014, 1, '', '', null, 'F', 1, 1, 'supplier:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2016, '新增', 2014, 2, '', '', null, 'F', 1, 1, 'supplier:add', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2017, '修改', 2014, 3, '', '', null, 'F', 1, 1, 'supplier:edit', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2018, '状态', 2014, 4, '', '', null, 'F', 1, 1, 'supplier:status', '#', null, 'admin', sysdate(), null, '', null, '');
