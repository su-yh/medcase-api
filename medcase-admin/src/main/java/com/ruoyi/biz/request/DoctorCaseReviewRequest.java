package com.ruoyi.biz.request;

import com.ruoyi.biz.enums.DoctorCaseStatusEnums;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 病例审核请求
 *
 * @author suyh
 */
@Data
public class DoctorCaseReviewRequest {
    @NotNull(message = "审核状态不能为空")
    private DoctorCaseStatusEnums status;

    @Size(max = 500, message = "审核拒绝原因不能超过500个字符")
    private String reason;
}
