package com.medcase.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户和岗位关联表实体。
 */
@Data
@TableName(value = "sys_user_post", autoResultMap = true)
public class SysUserPostEntity {

    private Long userId;

    private Long postId;
}
