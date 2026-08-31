package com.medcase.web.controller.system.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 首页公告响应数据。
 */
@Getter
@AllArgsConstructor
public class NoticeTopResponse {

    private List<NoticeTopItemResponse> data;
    private long unreadCount;
}
