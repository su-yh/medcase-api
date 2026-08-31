package com.medcase.web.controller.system.dto;

import lombok.Data;

/**
 * 公告查询请求。
 */
@Data
public class NoticeQueryRequest {

    private String noticeTitle;

    private String noticeType;

    private String createBy;
}
