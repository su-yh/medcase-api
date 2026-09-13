package com.medcase.system.mapper;

import com.medcase.common.constant.UserConstants;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysNoticeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysNoticeMapper extends BaseMapperX<SysNoticeEntity> {
    default List<SysNoticeEntity> selectTopNoticeList(Integer limit) {
        if (limit == null || limit <= 0) {
            return List.of();
        }
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(1);
        pageParam.setPageSize(limit);
        pageParam.setSearchCount(false);
        LambdaQueryWrapperX<SysNoticeEntity> query = build();
        query.eq(SysNoticeEntity::getStatus, UserConstants.NORMAL);
        query.orderByDesc(SysNoticeEntity::getNoticeId);
        return selectPage(pageParam, query).getList();
    }

    default PageResult<SysNoticeEntity> selectPage(
            PageParam pageParam, String noticeTitle, String noticeType, String createBy) {
        LambdaQueryWrapperX<SysNoticeEntity> query = build();
        query.likeIfPresent(SysNoticeEntity::getNoticeTitle, noticeTitle);
        query.eqIfPresent(SysNoticeEntity::getNoticeType, noticeType);
        query.likeIfPresent(SysNoticeEntity::getCreateBy, createBy);
        query.orderByDesc(SysNoticeEntity::getNoticeId);
        return selectPage(pageParam, query);
    }

}
