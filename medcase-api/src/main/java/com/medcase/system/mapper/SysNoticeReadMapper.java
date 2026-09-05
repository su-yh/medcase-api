package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.entity.SysNoticeReadEntity;
import com.medcase.web.controller.system.dto.NoticeReadUserResponse;
import com.medcase.web.controller.system.dto.NoticeTopItemResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper
public interface SysNoticeReadMapper extends BaseMapperX<SysNoticeReadEntity> {
    int selectUnreadCount(Long userId);

    List<NoticeTopItemResponse> selectTopNoticeList(@Param("limit") int limit);

    Set<Long> selectReadNoticeIds(
            @Param("userId") Long userId, @Param("noticeIds") Collection<Long> noticeIds);

    List<NoticeReadUserResponse> selectReadUsersByNoticeId(
            @Param("noticeId") Long noticeId, @Param("nickNameLike") String nickNameLike);

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
