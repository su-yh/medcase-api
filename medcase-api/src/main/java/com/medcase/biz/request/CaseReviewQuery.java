package com.medcase.biz.request;

import com.medcase.biz.enums.CaseStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import lombok.Data;

/**
 * 病例审核查询条件
 *
 * @author suyh
 */
@Data
public class CaseReviewQuery {
    private Long id;

    private String caseName;

    private CaseStatusEnums status;

    private UserTypeEnums userType;
}
