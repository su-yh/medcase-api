package com.medcase.web.controller.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Data;

import java.util.Date;

/**
 * 首页公告项响应。
 */
@Data
public class NoticeTopItemResponse {

    private Long noticeId;

    private String noticeTitle;

    private String noticeType;

    private String status;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonProperty(value = "isRead", access = Access.READ_ONLY)
    private boolean read;
}
