package com.medcase.web.controller.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medcase.system.entity.SysPostEntity;
import lombok.Data;

import java.util.Date;

/**
 * 岗位响应。
 */
@Data
public class PostResponse {

    private Long postId;

    private String postCode;

    private String postName;

    private Integer postSort;

    private String status;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public static PostResponse fromEntity(SysPostEntity entity) {
        if (entity == null) {
            return null;
        }
        PostResponse response = new PostResponse();
        response.setPostId(entity.getPostId());
        response.setPostCode(entity.getPostCode());
        response.setPostName(entity.getPostName());
        response.setPostSort(entity.getPostSort());
        response.setStatus(entity.getStatus());
        response.setRemark(entity.getRemark());
        response.setCreateTime(entity.getCreateTime());
        return response;
    }
}
