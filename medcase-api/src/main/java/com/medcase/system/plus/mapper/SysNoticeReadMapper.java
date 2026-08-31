package com.medcase.system.plus.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.system.plus.entity.SysNoticeReadEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface SysNoticeReadMapper extends BaseMapperX<SysNoticeReadEntity> {
    default int insertNoticeRead(SysNoticeReadEntity entity) {
        return insert(entity);
    }

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
