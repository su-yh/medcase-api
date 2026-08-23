package com.ruoyi.biz.doctor.request;

import lombok.Data;

/**
 * 医生管理查询条件
 *
 * @author suyh
 */
@Data
public class DoctorUserQuery {
    private String name;

    private String username;

    private String phone;

    private String status;
}
