package com.medcase.web.controller.system.dto;

import java.util.List;
import com.medcase.system.domain.SysNotice;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 首页公告响应数据。
 */
@Getter
@AllArgsConstructor
public class NoticeTopResponse {

    private List<SysNotice> data;
    private long unreadCount;
}
