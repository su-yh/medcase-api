package com.medcase.web.controller.system.dto;

import com.medcase.common.xss.Xss;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 公告保存请求。
 */
@Data
public class NoticeSaveRequest {

    private Long noticeId;

    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 50, message = "公告标题不能超过50个字符")
    private String noticeTitle;

    private String noticeType;

    private String noticeContent;

    private String status;
}
