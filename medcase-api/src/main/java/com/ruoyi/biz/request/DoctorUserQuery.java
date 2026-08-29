package com.ruoyi.biz.request;

import lombok.Data;

/**
 * 医生管理查询条件。
 *
 * @author suyh
 */
@Data
public class DoctorUserQuery {
    private String nickName;

    private String phone;

    private String status;
}
