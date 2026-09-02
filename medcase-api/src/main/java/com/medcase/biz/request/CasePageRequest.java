package com.medcase.biz.request;

import com.medcase.mvc.advice.date.DateTimeFormatPlus;
import com.medcase.mvc.advice.date.OffsetUnit;
import com.medcase.biz.enums.CaseStatusEnums;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 * @author suyh
 */
@Data
public class CasePageRequest {
    private String caseNameLike;

    private CaseStatusEnums status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeLowerBound;

    @DateTimeFormatPlus(pattern = "yyyy-MM-dd", offset = 1, unit = OffsetUnit.DAY)
    private Date createTimeUpperBound;
}
