package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

/**
 * 菜单权限表实体。
 */
@Data
@TableName(value = "sys_menu", autoResultMap = true)
public class SysMenuEntity extends AbstractBaseEntity {

    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    private String menuName;

    private Long parentId;

    private Integer orderNum;

    private String path;

    private String component;

    private String query;

    private String routeName;

    private String isFrame;

    private String isCache;

    private String menuType;

    private String visible;

    private String status;

    private String perms;

    private String icon;

    private String createBy;

    private String updateBy;

    private String remark;
}
