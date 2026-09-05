package com.medcase.web.controller.system.dto;

import com.medcase.mvc.advice.date.DateTimeFormatPlus;
import com.medcase.mvc.advice.date.OffsetUnit;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 角色查询请求。
 */
@Data
public class RoleQueryRequest {

    private String roleNameLike;

    private String roleKeyLike;

    private String status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;

    @DateTimeFormatPlus(pattern = "yyyy-MM-dd", offset = 1, unit = OffsetUnit.DAY)
    private Date endTime;
}
