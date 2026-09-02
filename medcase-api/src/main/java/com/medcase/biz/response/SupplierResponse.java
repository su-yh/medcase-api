package com.medcase.biz.response;

import com.medcase.biz.domain.SupplierEntity;
import lombok.Data;

import java.util.Date;

/**
 * 供应商响应。
 */
@Data
public class SupplierResponse {
    private Long id;

    private String name;

    private String sex;

    private String phone;

    private String email;

    private String idCardNumber;

    private String status;

    private Date createTime;

    private String createBy;

    private Date updateTime;

    private String updateBy;

    private String remark;

    public static SupplierResponse fromEntity(SupplierEntity entity) {
        SupplierResponse response = new SupplierResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setSex(entity.getSex());
        response.setPhone(entity.getPhonenumber());
        response.setEmail(entity.getEmail());
        response.setIdCardNumber(entity.getIdCardNumber());
        response.setStatus(entity.getStatus());
        response.setCreateTime(entity.getCreateTime());
        response.setCreateBy(entity.getCreateBy());
        response.setUpdateTime(entity.getUpdateTime());
        response.setUpdateBy(entity.getUpdateBy());
        response.setRemark(entity.getRemark());
        return response;
    }
}
