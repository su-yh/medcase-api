package com.ruoyi.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mp.entity.AbstractBaseEntity;
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

    private UserTypeEnums userType;

    private String phonenumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String status;

    @TableLogic(value = "0", delval = "2")
    @JsonIgnore
    private Boolean delFlag;

    private String loginIp;

    private Date loginDate;

    private Date pwdUpdateDate;
}
