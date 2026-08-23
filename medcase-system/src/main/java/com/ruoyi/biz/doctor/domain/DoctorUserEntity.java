package com.ruoyi.biz.doctor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ruoyi.common.enums.UserTypeEnums;
import java.util.Date;
import lombok.Data;

/**
 * 医生用户对象 sys_user
 *
 * @author suyh
 */
@Data
@TableName(value = "sys_user", autoResultMap = true)
public class DoctorUserEntity {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    private Long deptId;

    private String userName;

    private String nickName;

    private UserTypeEnums userType;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String status;

    @TableLogic(value = "0", delval = "1")
    @JsonIgnore
    private String delFlag;

    private String loginIp;

    private Date loginDate;

    private Date pwdUpdateDate;
}
