-- 系统初始化数据基线。
-- 该文件只用于首次上线数据库，依赖 001 系统表结构和 002 业务表结构。

insert into sys_user (
  user_id, dept_id, user_name, nick_name, supplier_id, user_type,
  id_card_number, id_card_front, id_card_back, title, qualification_certificate,
  review_reason, email, phonenumber, sex, avatar, password, status, del_flag,
  pwd_update_date, remark, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time
) values (
  1, null, 'admin', '超级管理员', null, '00',
  null, null, null, null, null,
  null, 'ry@163.com', '15888888888', '1', '',
  '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2',
  '0', 0, sysdate(), '管理员', null, 'admin', sysdate(), null, '', null
);

insert into sys_post (
  post_id, post_code, post_name, post_sort, enabled, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (1, 'ceo', '董事长', 1, 1, null, null, 'admin', sysdate(), null, '', null);

insert into sys_post (
  post_id, post_code, post_name, post_sort, enabled, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (2, 'se', '项目经理', 2, 1, null, null, 'admin', sysdate(), null, '', null);

insert into sys_post (
  post_id, post_code, post_name, post_sort, enabled, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (3, 'hr', '人力资源', 3, 1, null, null, 'admin', sysdate(), null, '', null);

insert into sys_post (
  post_id, post_code, post_name, post_sort, enabled, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (4, 'user', '普通员工', 4, 1, null, null, 'admin', sysdate(), null, '', null);

insert into sys_user_post (user_id, post_id) values (1, 1);

insert into sys_config (
  config_id, config_name, config_key, config_value, built_in, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 1, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', null, 'admin', sysdate(), null, '', null);

insert into sys_config (
  config_id, config_name, config_key, config_value, built_in, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 1, '初始化密码 123456', null, 'admin', sysdate(), null, '', null);

insert into sys_config (
  config_id, config_name, config_key, config_value, built_in, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 1, '深色主题theme-dark，浅色主题theme-light', null, 'admin', sysdate(), null, '', null);

insert into sys_config (
  config_id, config_name, config_key, config_value, built_in, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 1, '是否开启验证码功能（true开启，false关闭）', null, 'admin', sysdate(), null, '', null);

insert into sys_config (
  config_id, config_name, config_key, config_value, built_in, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 1, '是否开启注册用户功能（true开启，false关闭）', null, 'admin', sysdate(), null, '', null);

insert into sys_config (
  config_id, config_name, config_key, config_value, built_in, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 1, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）', null, 'admin', sysdate(), null, '', null);

insert into sys_config (
  config_id, config_name, config_key, config_value, built_in, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (7, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 1, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框', null, 'admin', sysdate(), null, '', null);

insert into sys_config (
  config_id, config_name, config_key, config_value, built_in, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (8, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 1, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框', null, 'admin', sysdate(), null, '', null);

insert into sys_config (
  config_id, config_name, config_key, config_value, built_in, remark,
  create_user_id, create_by, create_time, update_user_id, update_by, update_time
) values (9, '用户管理-密码字符范围', 'sys.account.chrtype', '0', 1, '默认任意字符范围，0任意（密码可以输入任意字符），1数字（密码只能为0-9数字），2英文字母（密码只能为a-z和A-Z字母），3字母和数字（密码必须包含字母，数字）,4字母数字和特殊字符（目前支持的特殊字符包括：~!@#$%^&*()-=_+）', null, 'admin', sysdate(), null, '', null);

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1, '系统管理', 0, 1000, 'system', null, null, 'M', 1, 1, '', 'system', null, 'admin', sysdate(), null, '', null, '系统管理目录');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (2, '系统监控', 0, 2000, 'monitor', null, null, 'M', 1, 1, '', 'monitor', null, 'admin', sysdate(), null, '', null, '系统监控目录');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (100, '用户管理', 1, 1000, 'user', 'system/user/index', 'SystemUser', 'C', 1, 1, 'system:user:list', 'user', null, 'admin', sysdate(), null, '', null, '用户管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (101, '角色管理', 1, 2000, 'role', 'system/role/index', 'SystemRole', 'C', 1, 1, 'system:role:list', 'peoples', null, 'admin', sysdate(), null, '', null, '角色管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (102, '菜单管理', 1, 3000, 'menu', 'system/menu/index', 'SystemMenu', 'C', 1, 1, 'system:menu:list', 'tree-table', null, 'admin', sysdate(), null, '', null, '菜单管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (103, '部门管理', 1, 4000, 'dept', 'system/dept/index', 'SystemDept', 'C', 1, 1, 'system:dept:list', 'tree', null, 'admin', sysdate(), null, '', null, '部门管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (104, '岗位管理', 1, 5000, 'post', 'system/post/index', 'SystemPost', 'C', 1, 1, 'system:post:list', 'post', null, 'admin', sysdate(), null, '', null, '岗位管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (105, '字典管理', 1, 6000, 'dict', 'system/dict/index', 'SystemDict', 'C', 1, 1, 'system:dict:list', 'dict', null, 'admin', sysdate(), null, '', null, '字典管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (106, '参数设置', 1, 7000, 'config', 'system/config/index', 'SystemConfig', 'C', 1, 1, 'system:config:list', 'edit', null, 'admin', sysdate(), null, '', null, '参数设置菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (107, '通知公告', 1, 8000, 'notice', 'system/notice/index', 'SystemNotice', 'C', 1, 1, 'system:notice:list', 'message', null, 'admin', sysdate(), null, '', null, '通知公告菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (108, '日志管理', 1, 9000, 'log', '', null, 'M', 1, 1, '', 'log', null, 'admin', sysdate(), null, '', null, '日志管理菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (109, '在线用户', 2, 1000, 'online', 'monitor/online/index', 'MonitorOnline', 'C', 1, 1, 'monitor:online:list', 'online', null, 'admin', sysdate(), null, '', null, '在线用户菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (112, '服务监控', 2, 4000, 'server', 'monitor/server/index', 'MonitorServer', 'C', 1, 1, 'monitor:server:list', 'server', null, 'admin', sysdate(), null, '', null, '服务监控菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (113, '缓存监控', 2, 5000, 'cache', 'monitor/cache/index', 'MonitorCache', 'C', 1, 1, 'monitor:cache:list', 'redis', null, 'admin', sysdate(), null, '', null, '缓存监控菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (114, '缓存列表', 2, 6000, 'cacheList', 'monitor/cache/list', 'MonitorCacheList', 'C', 1, 1, 'monitor:cache:list', 'redis-list', null, 'admin', sysdate(), null, '', null, '缓存列表菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (500, '操作日志', 108, 1000, 'operlog', 'monitor/operlog/index', 'MonitorOperlog', 'C', 1, 1, 'monitor:operlog:list', 'form', null, 'admin', sysdate(), null, '', null, '操作日志菜单');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1000, '用户查询', 100, 1, '', '', null, 'F', 1, 1, 'system:user:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1001, '用户新增', 100, 2, '', '', null, 'F', 1, 1, 'system:user:add', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1002, '用户修改', 100, 3, '', '', null, 'F', 1, 1, 'system:user:edit', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1003, '用户删除', 100, 4, '', '', null, 'F', 1, 1, 'system:user:remove', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1006, '重置密码', 100, 7, '', '', null, 'F', 1, 1, 'system:user:resetPwd', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1007, '角色查询', 101, 1, '', '', null, 'F', 1, 1, 'system:role:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1008, '角色新增', 101, 2, '', '', null, 'F', 1, 1, 'system:role:add', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1009, '角色修改', 101, 3, '', '', null, 'F', 1, 1, 'system:role:edit', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1010, '角色删除', 101, 4, '', '', null, 'F', 1, 1, 'system:role:remove', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1012, '菜单查询', 102, 1, '', '', null, 'F', 1, 1, 'system:menu:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1013, '菜单新增', 102, 2, '', '', null, 'F', 1, 1, 'system:menu:add', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1014, '菜单修改', 102, 3, '', '', null, 'F', 1, 1, 'system:menu:edit', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1015, '菜单删除', 102, 4, '', '', null, 'F', 1, 1, 'system:menu:remove', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1016, '部门查询', 103, 1, '', '', null, 'F', 1, 1, 'system:dept:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1017, '部门新增', 103, 2, '', '', null, 'F', 1, 1, 'system:dept:add', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1018, '部门修改', 103, 3, '', '', null, 'F', 1, 1, 'system:dept:edit', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1019, '部门删除', 103, 4, '', '', null, 'F', 1, 1, 'system:dept:remove', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1020, '岗位查询', 104, 1, '', '', null, 'F', 1, 1, 'system:post:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1021, '岗位新增', 104, 2, '', '', null, 'F', 1, 1, 'system:post:add', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1022, '岗位修改', 104, 3, '', '', null, 'F', 1, 1, 'system:post:edit', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1023, '岗位删除', 104, 4, '', '', null, 'F', 1, 1, 'system:post:remove', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1025, '字典查询', 105, 1, '#', '', null, 'F', 1, 1, 'system:dict:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1026, '字典新增', 105, 2, '#', '', null, 'F', 1, 1, 'system:dict:add', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1027, '字典修改', 105, 3, '#', '', null, 'F', 1, 1, 'system:dict:edit', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1028, '字典删除', 105, 4, '#', '', null, 'F', 1, 1, 'system:dict:remove', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1030, '参数查询', 106, 1, '#', '', null, 'F', 1, 1, 'system:config:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1031, '参数新增', 106, 2, '#', '', null, 'F', 1, 1, 'system:config:add', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1032, '参数修改', 106, 3, '#', '', null, 'F', 1, 1, 'system:config:edit', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1033, '参数删除', 106, 4, '#', '', null, 'F', 1, 1, 'system:config:remove', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1035, '公告查询', 107, 1, '#', '', null, 'F', 1, 1, 'system:notice:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1036, '公告新增', 107, 2, '#', '', null, 'F', 1, 1, 'system:notice:add', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1037, '公告修改', 107, 3, '#', '', null, 'F', 1, 1, 'system:notice:edit', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1038, '公告删除', 107, 4, '#', '', null, 'F', 1, 1, 'system:notice:remove', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1039, '操作查询', 500, 1, '#', '', null, 'F', 1, 1, 'monitor:operlog:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1046, '在线查询', 109, 1, '#', '', null, 'F', 1, 1, 'monitor:online:query', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1047, '批量强退', 109, 2, '#', '', null, 'F', 1, 1, 'monitor:online:batchLogout', '#', null, 'admin', sysdate(), null, '', null, '');

insert into sys_menu (
  id, menu_name, parent_id, order_num, route_path, vue_component_path, route_name,
  menu_type, visible, enabled, perms, icon, create_user_id, create_by, create_time,
  update_user_id, update_by, update_time, remark
) values (1048, '单条强退', 109, 3, '#', '', null, 'F', 1, 1, 'monitor:online:forceLogout', '#', null, 'admin', sysdate(), null, '', null, '');
