package com.medcase.system.plus.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.domain.SysNotice;
import com.medcase.system.plus.entity.SysNoticeReadEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper
public interface SysNoticeReadMapper extends BaseMapperX<SysNoticeReadEntity> {
    int selectUnreadCount(Long userId);

    List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit);

    List<Map<String, Object>> selectReadUsersByNoticeId(Long noticeId, String searchValue);

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
