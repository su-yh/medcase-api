-- 删除旧操作日志菜单按钮权限。
delete from sys_role_menu where menu_id = 1040;
delete from sys_menu where id = 1040;

-- 旧操作日志表已由 sys_audit_record 替代。
drop table if exists sys_oper_log;
