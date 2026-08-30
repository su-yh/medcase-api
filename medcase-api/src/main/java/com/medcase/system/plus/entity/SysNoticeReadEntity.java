package com.medcase.system.plus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 公告已读记录表实体。
 */
@Data
@TableName(value = "sys_notice_read", autoResultMap = true)
public class SysNoticeReadEntity {

    @TableId(value = "read_id", type = IdType.AUTO)
    private Long readId;

    private Long noticeId;

    private Long userId;

    private Date readTime;
}
