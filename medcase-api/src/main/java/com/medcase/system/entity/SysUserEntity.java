package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.entity.AbstractBaseEntity;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户信息表实体。
 */
@Data
@TableName(value = "sys_user", autoResultMap = true)
public class SysUserEntity extends AbstractBaseEntity {

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    private Long deptId;

    private String userName;

    private String nickName;

    private Long supplierId;

    private UserTypeEnums userType;

    private String idCardNumber;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private FileAttachment idCardFront;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private FileAttachment idCardBack;

    private String title;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private FileAttachment qualificationCertificate;

    private String reviewReason;

    private String email;

    private String phonenumber;

    private String sex;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private FileAttachment avatar;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String status;

    private String delFlag;

    private Date pwdUpdateDate;

    private String remark;

    @TableField(exist = false)
    private SysDeptEntity dept;

    @TableField(exist = false)
    private List<SysRoleEntity> roles;

    @TableField(exist = false)
    private Long[] roleIds;

    @TableField(exist = false)
    private Long[] postIds;

    @TableField(exist = false)
    private Long roleId;

    public boolean isAdmin() {
        return userId != null && userId == 1L;
    }
}
