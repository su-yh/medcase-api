package com.ruoyi.biz.request;

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
    @NotNull(message = "审核是否通过")
    private Boolean approve;

    @Size(max = 500, message = "审核拒绝原因不能超过500个字符")
    private String reason;
}
