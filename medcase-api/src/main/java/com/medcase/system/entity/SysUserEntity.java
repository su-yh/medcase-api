package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.entity.AbstractBaseEntity;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

import java.util.Date;

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

    private String password;

    private String status;

    private String delFlag;

    private String loginIp;

    private Date loginDate;

    private Date pwdUpdateDate;

    private String createBy;

    private String updateBy;

    private String remark;
}
