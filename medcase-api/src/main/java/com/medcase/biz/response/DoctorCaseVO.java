package com.medcase.biz.response;

import com.medcase.storage.pojo.FileAttachment;
import com.medcase.biz.domain.DoctorCaseEntity;
import com.medcase.biz.enums.DoctorCaseStatusEnums;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 医生病例返回对象
 *
 * @author suyh
 */
@Data
public class DoctorCaseVO {
    private Long id;

    private String userNickname;

    private String caseName;

    private String content;

    private List<FileAttachment> attachments;

    private DoctorCaseStatusEnums status;

    private String statusDesc;

    private String reviewReason;

    private String reviewerNickname;

    private Date reviewTime;

    private String settlerNickname;

    private Date settledTime;

    private Date createTime;

    public static DoctorCaseVO fromEntity(DoctorCaseEntity entity) {
        DoctorCaseVO result = new DoctorCaseVO();
        result.setId(entity.getId());
        result.setUserNickname(entity.getUserNickname());
        result.setCaseName(entity.getCaseName());
        result.setContent(entity.getContent());
        result.setAttachments(entity.getAttachments());
        result.setStatus(entity.getStatus());
        result.setStatusDesc(entity.getStatus() == null ? null : entity.getStatus().getDesc());
        result.setReviewReason(entity.getReviewReason());
        result.setReviewerNickname(entity.getReviewerNickname());
        result.setReviewTime(entity.getReviewTime());
        result.setSettlerNickname(entity.getSettlerNickname());
        result.setSettledTime(entity.getSettledTime());
        result.setCreateTime(entity.getCreateTime());
        return result;
    }
}
