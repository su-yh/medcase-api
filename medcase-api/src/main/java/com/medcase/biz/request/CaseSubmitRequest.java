package com.medcase.biz.request;

import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

import java.util.List;

/**
 * 医生病例提交请求
 *
 * @author suyh
 */
@Data
public class CaseSubmitRequest {
    private Long id;

    private String caseName;

    private String content;

    private List<FileAttachment> attachments;
}
