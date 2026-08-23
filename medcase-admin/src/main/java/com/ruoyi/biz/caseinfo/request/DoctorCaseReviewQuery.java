package com.ruoyi.biz.caseinfo.request;

import com.ruoyi.biz.caseinfo.enums.DoctorCaseStatusEnums;
import lombok.Data;

/**
 * 病例审核查询条件
 *
 * @author suyh
 */
@Data
public class DoctorCaseReviewQuery {
    private Long id;

    private String title;

    private DoctorCaseStatusEnums status;
}
