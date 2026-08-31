package com.medcase.system.plus.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.system.plus.entity.SysNoticeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Arrays;
import java.util.List;

@Mapper
public interface SysNoticeMapper extends BaseMapperX<SysNoticeEntity> {
    default SysNoticeEntity selectNoticeById(Long noticeId) {
        return selectById(noticeId);
    }

    default List<SysNoticeEntity> selectNoticeList(
            String noticeTitle, String noticeType, String createBy) {
        LambdaQueryWrapper<SysNoticeEntity> query = build()
                .likeIfPresent(SysNoticeEntity::getNoticeTitle, noticeTitle)
                .eqIfPresent(SysNoticeEntity::getNoticeType, noticeType)
                .likeIfPresent(SysNoticeEntity::getCreateBy, createBy)
                .orderByDesc(SysNoticeEntity::getNoticeId);
        return selectList(query);
    }

    default int insertNotice(SysNoticeEntity entity) {
        return insert(entity);
    }

    default int updateNotice(SysNoticeEntity entity) {
        return updateById(entity);
    }

    default int deleteNoticeById(Long noticeId) {
        return deleteById(noticeId);
    }

    default int deleteNoticeByIds(Long[] noticeIds) {
        return deleteBatchIds(Arrays.asList(noticeIds));
    }
}
