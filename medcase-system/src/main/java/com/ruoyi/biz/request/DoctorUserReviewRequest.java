package com.ruoyi.biz.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 医生用户审核请求。
 *
 * @author suyh
 */
@Data
public class DoctorUserReviewRequest {
    @NotNull(message = "审核是否通过")
    private Boolean approve;
}
