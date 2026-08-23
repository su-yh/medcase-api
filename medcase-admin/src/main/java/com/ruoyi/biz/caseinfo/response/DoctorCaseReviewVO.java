package com.ruoyi.biz.caseinfo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.biz.caseinfo.domain.DoctorCaseEntity;
import com.ruoyi.biz.caseinfo.domain.FileAttachment;
import com.ruoyi.biz.caseinfo.enums.DoctorCaseStatusEnums;
import java.util.Date;
import java.util.List;
import lombok.Data;

/**
 * 病例审核返回对象
 *
 * @author suyh
 */
@Data
public class DoctorCaseReviewVO {
    private Long id;

    private Long doctorId;

    private String doctorName;

    private String title;

    private String remark;

    private List<FileAttachment> attachments;

    private DoctorCaseStatusEnums status;

    private String statusDesc;

    private String reviewReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reviewTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date settledTime;

    public static DoctorCaseReviewVO fromEntity(DoctorCaseEntity entity) {
        DoctorCaseReviewVO result = new DoctorCaseReviewVO();
        result.setId(entity.getId());
        result.setDoctorId(entity.getDoctorId());
        result.setDoctorName(entity.getDoctorNickname());
        result.setTitle(entity.getTitle());
        result.setRemark(entity.getRemark());
        result.setAttachments(entity.getAttachments());
        result.setStatus(entity.getStatus());
        result.setStatusDesc(entity.getStatus() == null ? null : entity.getStatus().getDesc());
        result.setReviewReason(entity.getReviewReason());
        result.setCreateTime(entity.getCreateTime());
        result.setSubmitTime(entity.getSubmitTime());
        result.setReviewTime(entity.getReviewTime());
        result.setSettledTime(entity.getSettledTime());
        return result;
    }
}
