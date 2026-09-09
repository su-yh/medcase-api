package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表实体。
 */
@Data
@TableName(value = "sys_menu", autoResultMap = true)
public class SysMenuEntity extends AbstractBaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String menuName;

    private Long parentId;

    private Integer orderNum;

    private String routePath;

    private String vueComponentPath;

    private String routeName;

    private String isCache;

    private String menuType;

    private boolean visible;

    private String status;

    private String perms;

    private String icon;

    private String remark;

    @TableField(exist = false)
    private String parentName;

    @TableField(exist = false)
    private List<SysMenuEntity> children = new ArrayList<>();
}
