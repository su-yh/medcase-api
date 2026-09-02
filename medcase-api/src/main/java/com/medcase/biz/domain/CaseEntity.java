package com.medcase.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.medcase.biz.enums.CaseStatusEnums;
import com.medcase.mp.entity.AbstractBaseEntity;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 病例实体 medcase_case
 *
 * @author suyh
 */
@Data
@TableName(value = "medcase_case", autoResultMap = true)
public class CaseEntity extends AbstractBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String userNickname;

    private UserTypeEnums userType;

    private String caseName;

    private String content;

    @TableField(value = "attachments", typeHandler = JacksonTypeHandler.class)
    private List<FileAttachment> attachments;

    private CaseStatusEnums status;

    private String reviewReason;

    private Long reviewerId;

    private String reviewerNickname;

    private Long settlerId;

    private String settlerNickname;

    private Date submitTime;

    private Date reviewTime;

    private Date settledTime;

    @TableLogic
    private Boolean deleteFlag;
}
