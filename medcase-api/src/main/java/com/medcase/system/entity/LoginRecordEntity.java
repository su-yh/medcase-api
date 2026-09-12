package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.common.enums.LoginRecordStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import lombok.Data;

import java.util.Date;

/**
 * 登录记录实体。
 */
@Data
@TableName(value = "sys_login_record", autoResultMap = true)
public class LoginRecordEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private UserTypeEnums userType;

    private String userName;

    private LoginRecordStatusEnums status;

    private String ipaddr;

    private String loginLocation;

    private String browser;

    private String os;

    private String msg;

    private Date loginTime;
}
