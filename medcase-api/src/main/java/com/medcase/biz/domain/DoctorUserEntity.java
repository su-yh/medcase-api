package com.medcase.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.entity.AbstractBaseEntity;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

import java.util.Date;

/**
 * 医生用户对象 sys_user
 *
 * @author suyh
 */
@Data
@TableName(value = "sys_user", autoResultMap = true)
public class DoctorUserEntity extends AbstractBaseEntity {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    private String userName;

    private String nickName;

    private String sex;

    private String idCardNumber;

    private String title;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private FileAttachment idCardFront;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private FileAttachment idCardBack;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private FileAttachment qualificationCertificate;

    private UserTypeEnums userType;

    private String phonenumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private UserStatusEnums status;

    private String reviewReason;

    @TableLogic(value = "0", delval = "2")
    @JsonIgnore
    private Boolean delFlag;

    private String loginIp;

    private Date loginDate;

    private Date pwdUpdateDate;
}
