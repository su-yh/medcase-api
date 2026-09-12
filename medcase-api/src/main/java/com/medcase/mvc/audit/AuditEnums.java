package com.medcase.mvc.audit;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author suyh
 * @since 2026-09-06
 */
@Getter
public enum AuditEnums implements IAudit {
    CREATE_ADMIN_USER("create_admin_user", "创建后台用户"),
    UPDATE_ADMIN_USER("update_admin_user", "修改后台用户"),
    DELETE_ADMIN_USER("delete_admin_user", "删除后台用户"),
    RESET_ADMIN_USER_PASSWORD("reset_admin_user_password", "重置后台用户密码"),
    UPDATE_ADMIN_USER_STATUS("update_admin_user_status", "修改后台用户状态"),
    UPDATE_ADMIN_USER_ROLES("update_admin_user_roles", "修改后台用户角色"),
    CREATE_ROLE("create_role", "创建角色"),
    UPDATE_ROLE("update_role", "修改角色"),
    UPDATE_ROLE_STATUS("update_role_status", "修改角色状态"),
    UPDATE_ROLE_MENUS("update_role_menus", "修改角色菜单"),
    DELETE_ROLE("delete_role", "删除角色"),
    CANCEL_ROLE_USER("cancel_role_user", "取消角色授权用户"),
    CANCEL_ROLE_USERS("cancel_role_users", "批量取消角色授权用户"),
    SELECT_ROLE_USERS("select_role_users", "批量选择角色授权用户"),
    CREATE_CONFIG("create_config", "创建参数"),
    UPDATE_CONFIG("update_config", "修改参数"),
    DELETE_CONFIG("delete_config", "删除参数"),
    REFRESH_CONFIG_CACHE("refresh_config_cache", "刷新参数缓存"),
    CREATE_MENU("create_menu", "创建菜单"),
    UPDATE_MENU("update_menu", "修改菜单"),
    UPDATE_MENU_SORT("update_menu_sort", "保存菜单排序"),
    DELETE_MENU("delete_menu", "删除菜单"),
    CREATE_POST("create_post", "创建岗位"),
    UPDATE_POST("update_post", "修改岗位"),
    DELETE_POST("delete_post", "删除岗位"),
    CREATE_DICT_DATA("create_dict_data", "创建字典数据"),
    UPDATE_DICT_DATA("update_dict_data", "修改字典数据"),
    DELETE_DICT_DATA("delete_dict_data", "删除字典数据"),
    CREATE_DEPT("create_dept", "创建部门"),
    UPDATE_DEPT("update_dept", "修改部门"),
    UPDATE_DEPT_SORT("update_dept_sort", "保存部门排序"),
    DELETE_DEPT("delete_dept", "删除部门"),
    CREATE_DICT_TYPE("create_dict_type", "创建字典类型"),
    UPDATE_DICT_TYPE("update_dict_type", "修改字典类型"),
    DELETE_DICT_TYPE("delete_dict_type", "删除字典类型"),
    REFRESH_DICT_CACHE("refresh_dict_cache", "刷新字典缓存"),
    CREATE_NOTICE("create_notice", "创建通知公告"),
    UPDATE_NOTICE("update_notice", "修改通知公告"),
    DELETE_NOTICE("delete_notice", "删除通知公告"),
    UPDATE_ADMIN_PROFILE("update_admin_profile", "修改个人信息"),
    UPDATE_ADMIN_PASSWORD("update_admin_password", "修改个人密码"),
    FORCE_LOGOUT_ONLINE_USER("force_logout_online_user", "强退在线用户"),
    ;

    @EnumValue
    private final String code;
    private final String desc;

    AuditEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }
}
