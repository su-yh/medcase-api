-- 删除导入导出菜单及权限
DELETE FROM sys_role_menu
WHERE menu_id IN (
    SELECT menu_id
    FROM sys_menu
    WHERE perms LIKE '%:import'
       OR perms LIKE '%:export'
);

DELETE FROM sys_menu
WHERE perms LIKE '%:import'
   OR perms LIKE '%:export';

-- 删除导入导出操作类型字典
DELETE FROM sys_dict_data
WHERE dict_type = 'sys_oper_type'
  AND dict_value IN ('5', '6');
