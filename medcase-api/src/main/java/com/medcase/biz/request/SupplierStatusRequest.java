package com.medcase.biz.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 供应商状态请求。
 */
@Data
public class SupplierStatusRequest {
    @NotNull(message = "供应商状态不能为空")
    private Boolean status;
}
