package com.ruoyi.biz.caseinfo.request;

import com.ruoyi.mvc.advice.date.DateTimeFormatPlus;
import com.ruoyi.mvc.advice.date.OffsetUnit;
import com.ruoyi.biz.caseinfo.enums.DoctorCaseStatusEnums;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 * @author suyh
 */
@Data
public class DoctorCasePageRequest {
    private String titleLike;

    private DoctorCaseStatusEnums status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeLowerBound;

    @DateTimeFormatPlus(pattern = "yyyy-MM-dd", offset = 1, unit = OffsetUnit.DAY)
    private Date createTimeUpperBound;
}
