package com.medcase.system.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.medcase.common.core.domain.BaseEntity;
import com.medcase.common.xss.Xss;
import lombok.Data;

/**
 * 通知公告表 sys_notice
 * 
 */
@Data
public class SysNotice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 公告ID */
    private Long noticeId;

    /** 公告标题 */
    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符")
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    private String noticeType;

    /** 公告内容 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    private String status;

    /** 是否已读 */
    @JsonProperty("isRead")
    private boolean isRead;

    public boolean getIsRead() {

        return isRead;
    }
}
