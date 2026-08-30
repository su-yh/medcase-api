package com.medcase.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.medcase.biz.enums.DoctorCaseStatusEnums;
import com.medcase.mp.entity.AbstractBaseEntity;
import com.medcase.storage.pojo.FileAttachment;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 医生病例实体 medcase_doctor_case
 *
 * @author suyh
 */
@Data
@TableName(value = "medcase_doctor_case", autoResultMap = true)
public class DoctorCaseEntity extends AbstractBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long doctorId;

    private String doctorNickname;

    private String caseName;

    private String content;

    @TableField(value = "attachments", typeHandler = JacksonTypeHandler.class)
    private List<FileAttachment> attachments;

    private DoctorCaseStatusEnums status;

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
