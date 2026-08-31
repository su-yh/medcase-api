package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.entity.SysNoticeReadEntity;
import com.medcase.web.controller.system.dto.NoticeReadUserResponse;
import com.medcase.web.controller.system.dto.NoticeTopItemResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface SysNoticeReadMapper extends BaseMapperX<SysNoticeReadEntity> {
    int selectUnreadCount(Long userId);

    List<NoticeTopItemResponse> selectNoticeListWithReadStatus(Long userId, int limit);

    List<NoticeReadUserResponse> selectReadUsersByNoticeId(Long noticeId, String searchValue);

    default void insertNoticeReadBatch(Collection<SysNoticeReadEntity> entities) {
        insertBatch(entities);
    }

    default int deleteByNoticeIds(Collection<Long> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return 0;
        }
        return delete(build().in(SysNoticeReadEntity::getNoticeId, noticeIds));
    }
}
