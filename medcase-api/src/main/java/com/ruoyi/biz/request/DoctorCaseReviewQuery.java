package com.ruoyi.biz.request;

import com.ruoyi.biz.enums.DoctorCaseStatusEnums;
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
