package com.ruoyi.web.controller.doctor.response;

import com.ruoyi.web.domain.DoctorCaseEntity;
import com.ruoyi.web.enums.DoctorCaseStatusEnums;
import lombok.Data;

import java.util.Date;

/**
 * 医生病例返回对象
 *
 * @author suyh
 */
@Data
public class DoctorCaseVO {
    private Long id;

    private String doctorNickname;

    private String caseContent;

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
        result.setCaseContent(entity.getCaseContent());
        result.setStatus(entity.getStatus());
        result.setStatusDesc(entity.getStatus() == null ? null : entity.getStatus().getDesc());
        result.setReviewReason(entity.getReviewReason());
        result.setReviewTime(entity.getReviewTime());
        result.setSettledTime(entity.getSettledTime());
        result.setCreateTime(entity.getCreateTime());
        return result;
    }
}
