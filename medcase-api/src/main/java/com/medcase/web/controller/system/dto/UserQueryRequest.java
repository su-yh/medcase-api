package com.medcase.web.controller.system.dto;

import com.medcase.common.enums.UserTypeEnums;
import lombok.Data;

/**
 * 用户查询请求。
 */
@Data
public class UserQueryRequest {

    private Long userId;

    private Long deptId;

    private Long roleId;

    private String userName;

    private String nickName;

    private UserTypeEnums userType;

    private String phonenumber;

    private String status;
}
