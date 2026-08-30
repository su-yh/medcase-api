package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medcase.mp.entity.AbstractBaseEntity;
import lombok.Data;

/**
 * 通知公告表实体。
 */
@Data
@TableName(value = "sys_notice", autoResultMap = true)
public class SysNoticeEntity extends AbstractBaseEntity {

    @TableId(value = "notice_id", type = IdType.AUTO)
    private Long noticeId;

    private String noticeTitle;

    private String noticeType;

    private String noticeContent;

    private String status;

    private String createBy;

    private String updateBy;

    private String remark;
}
