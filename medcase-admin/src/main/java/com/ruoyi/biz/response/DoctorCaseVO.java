package com.ruoyi.biz.response;

import com.ruoyi.biz.domain.FileAttachment;
import com.ruoyi.biz.domain.DoctorCaseEntity;
import com.ruoyi.biz.enums.DoctorCaseStatusEnums;
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

    private String doctorNickname;

    private String title;

    private String remark;

    private List<FileAttachment> attachments;

    private DoctorCaseStatusEnums status;

    private String statusDesc;

    private String reviewReason;

    private Date reviewTime;

    private Date settledTime;

    private Date createTime;

    public static DoctorCaseVO fromEntity(DoctorCaseEntity entity) {
        DoctorCaseVO result = new DoctorCaseVO();
        result.setId(entity.getId());
        result.setDoctorNickname(entity.getDoctorNickname());
        result.setTitle(entity.getTitle());
        result.setRemark(entity.getRemark());
        result.setAttachments(entity.getAttachments());
        result.setStatus(entity.getStatus());
        result.setStatusDesc(entity.getStatus() == null ? null : entity.getStatus().getDesc());
        result.setReviewReason(entity.getReviewReason());
        result.setReviewTime(entity.getReviewTime());
        result.setSettledTime(entity.getSettledTime());
        result.setCreateTime(entity.getCreateTime());
        return result;
    }
}
