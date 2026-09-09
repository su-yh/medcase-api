package com.medcase.web.controller.system.dto;

import lombok.Data;

/**
 * 菜单查询请求。
 */
@Data
public class MenuQueryRequest {

    private String menuName;

    private Boolean visible;

    private String status;
}
