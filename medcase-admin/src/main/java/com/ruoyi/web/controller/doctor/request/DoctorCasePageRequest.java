package com.ruoyi.web.controller.doctor.request;

import com.ruoyi.mvc.advice.DateOffset;
import com.ruoyi.web.enums.DoctorCaseStatusEnums;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author suyh
 */
@Data
public class DoctorCasePageRequest {
    private String titleLike;

    private DoctorCaseStatusEnums status;

    private Date createTimeLowerBound;

    @DateOffset
    private Date createTimeUpperBound;
}
