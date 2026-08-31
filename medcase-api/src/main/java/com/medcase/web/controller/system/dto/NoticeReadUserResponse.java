package com.medcase.web.controller.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 公告已读用户响应。
 */
@Data
public class NoticeReadUserResponse {

    private Long userId;

    private String userName;

    private String nickName;

    private String deptName;

    private String phonenumber;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;
}
