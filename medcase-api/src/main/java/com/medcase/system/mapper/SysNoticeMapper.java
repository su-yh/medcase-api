package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysNoticeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysNoticeMapper extends BaseMapperX<SysNoticeEntity> {
    default PageResult<SysNoticeEntity> selectPage(
            PageParam pageParam, String noticeTitle, String noticeType, String createBy) {
        LambdaQueryWrapper<SysNoticeEntity> query = build()
                .likeIfPresent(SysNoticeEntity::getNoticeTitle, noticeTitle)
                .eqIfPresent(SysNoticeEntity::getNoticeType, noticeType)
                .likeIfPresent(SysNoticeEntity::getCreateBy, createBy)
                .orderByDesc(SysNoticeEntity::getNoticeId);
        return selectPage(pageParam, query);
    }

}
