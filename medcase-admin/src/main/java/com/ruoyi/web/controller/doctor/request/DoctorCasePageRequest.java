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

    @DateOffset(offsetDays = 0, description = "提交开始日期")
    private Date createTimeLowerBound;

    @DateOffset(description = "提交结束日期")
    private Date createTimeUpperBound;
}
