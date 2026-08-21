package com.ruoyi.web.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ruoyi.web.enums.DoctorCaseStatusEnums;
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
public class DoctorCaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long doctorId;

    private String doctorNickname;

    private String remark;

    @TableField(value = "attachments", typeHandler = JacksonTypeHandler.class)
    private List<FileAttachment> attachments;

    private DoctorCaseStatusEnums status;

    private String reviewReason;

    private Date reviewTime;

    private Date settledTime;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Boolean deleteFlag;
}
