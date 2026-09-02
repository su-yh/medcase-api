package com.medcase.biz.response;

import com.medcase.biz.domain.SupplierEntity;
import lombok.Data;

/**
 * 供应商选项响应。
 */
@Data
public class SupplierOptionResponse {
    private Long id;

    private String name;

    public static SupplierOptionResponse fromEntity(SupplierEntity entity) {
        SupplierOptionResponse response = new SupplierOptionResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        return response;
    }
}
