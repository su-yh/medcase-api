package com.medcase.biz.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 供应商状态请求。
 */
@Data
public class SupplierStatusRequest {
    @NotBlank(message = "供应商状态不能为空")
    private String status;
}
