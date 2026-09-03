package com.medcase.biz.request;

import com.medcase.biz.enums.SupplierStatusEnums;
import lombok.Data;

/**
 * 供应商查询条件。
 */
@Data
public class SupplierQuery {
    private String name;

    private String phone;

    private SupplierStatusEnums status;
}
