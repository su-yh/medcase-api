package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.system.entity.SysNoticeReadEntity;
import com.medcase.web.controller.system.dto.NoticeReadUserResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface SysNoticeReadMapper extends BaseMapperX<SysNoticeReadEntity> {
    int selectUnreadCount(Long userId);

    default Set<Long> selectReadNoticeIds(Long userId, Collection<Long> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return Set.of();
        }
        LambdaQueryWrapperX<SysNoticeReadEntity> queryWrapper = build();
        queryWrapper.select(SysNoticeReadEntity::getNoticeId);
        queryWrapper.eq(SysNoticeReadEntity::getUserId, userId);
        queryWrapper.in(SysNoticeReadEntity::getNoticeId, noticeIds);
        return selectList(queryWrapper).stream()
                .map(SysNoticeReadEntity::getNoticeId)
                .collect(Collectors.toSet());
    }

    List<NoticeReadUserResponse> selectReadUsersByNoticeId(
            @Param("noticeId") Long noticeId, @Param("nickNameLike") String nickNameLike);

    default void insertNoticeReadBatch(Collection<SysNoticeReadEntity> entities) {
        insertBatch(entities);
    }

    default int deleteByNoticeIds(Collection<Long> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) {
            return 0;
        }
        LambdaQueryWrapperX<SysNoticeReadEntity> queryWrapper = build();
        queryWrapper.in(SysNoticeReadEntity::getNoticeId, noticeIds);
        return delete(queryWrapper);
    }
}
