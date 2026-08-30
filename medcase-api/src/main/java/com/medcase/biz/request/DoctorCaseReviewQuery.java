package com.medcase.biz.request;

import com.medcase.biz.enums.DoctorCaseStatusEnums;
import lombok.Data;

/**
 * 病例审核查询条件
 *
 * @author suyh
 */
@Data
public class DoctorCaseReviewQuery {
    private Long id;

    private String caseName;

    private DoctorCaseStatusEnums status;
}
