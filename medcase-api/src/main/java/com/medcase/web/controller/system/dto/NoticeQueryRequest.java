package com.medcase.web.controller.system.dto;

import com.medcase.common.enums.NoticeTypeEnums;
import lombok.Data;

/**
 * 公告查询请求。
 */
@Data
public class NoticeQueryRequest {

    private String noticeTitle;

    private NoticeTypeEnums noticeType;

    private String createBy;
}
