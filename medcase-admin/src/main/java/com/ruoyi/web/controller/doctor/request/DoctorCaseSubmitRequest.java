package com.ruoyi.web.controller.doctor.request;

import com.ruoyi.web.domain.FileAttachment;
import lombok.Data;

import java.util.List;

/**
 * 医生病例提交请求
 *
 * @author suyh
 */
@Data
public class DoctorCaseSubmitRequest {
    private Long id;

    private String title;

    private String remark;

    private List<FileAttachment> attachments;
}
