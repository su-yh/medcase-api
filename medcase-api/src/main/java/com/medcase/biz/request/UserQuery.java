package com.medcase.biz.request;

import lombok.Data;

/**
 * 用户管理查询条件。
 *
 * @author suyh
 */
@Data
public class UserQuery {
    private Long supplierId;

    private String nickName;

    private String phone;

    private String status;
}
