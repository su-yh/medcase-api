package com.medcase.biz.request;

import lombok.Data;

/**
 * 供应商查询条件。
 */
@Data
public class SupplierQuery {
    private String nickName;

    private String phone;

    private String status;
}
