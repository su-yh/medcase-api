package com.medcase.web.controller.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medcase.system.entity.SysNoticeEntity;
import lombok.Data;

import java.util.Date;

/**
 * 公告响应。
 */
@Data
public class NoticeResponse {

    private Long noticeId;

    private String noticeTitle;

    private String noticeType;

    private String noticeContent;

    private String status;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public static NoticeResponse fromEntity(SysNoticeEntity entity) {
        if (entity == null) {
            return null;
        }
        NoticeResponse response = new NoticeResponse();
        response.setNoticeId(entity.getNoticeId());
        response.setNoticeTitle(entity.getNoticeTitle());
        response.setNoticeType(entity.getNoticeType());
        response.setNoticeContent(entity.getNoticeContent());
        response.setStatus(entity.getStatus());
        response.setCreateBy(entity.getCreateBy());
        response.setCreateTime(entity.getCreateTime());
        return response;
    }
}
