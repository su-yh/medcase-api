package com.medcase.system.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.system.domain.SysNotice;
import com.medcase.system.domain.SysNoticeRead;
import com.medcase.system.plus.SystemEntityConverter;
import com.medcase.system.plus.entity.SysNoticeReadEntity;
import com.medcase.system.plus.mapper.SysNoticeReadMapper;
import com.medcase.system.service.ISysNoticeReadService;

/**
 * 公告已读记录 服务层实现
 */
@Service
public class SysNoticeReadServiceImpl implements ISysNoticeReadService {

    @Autowired
    private com.medcase.system.mapper.SysNoticeReadHistoryMapper noticeReadHistoryMapper;

    @Autowired
    private SysNoticeReadMapper noticeReadMapper;

    /**
     * 标记已读
     */
    @Override
    public void markRead(Long noticeId, Long userId) {

        SysNoticeRead record = new SysNoticeRead();
        record.setNoticeId(noticeId);
        record.setUserId(userId);
        noticeReadMapper.insertNoticeRead(SystemEntityConverter.toEntity(record));
    }

    /**
     * 查询某用户未读公告数量
     */
    @Override
    public int selectUnreadCount(Long userId) {

        return noticeReadHistoryMapper.selectUnreadCount(userId);
    }

    /**
     * 查询公告列表并标记当前用户已读状态
     */
    @Override
    public List<SysNotice> selectNoticeListWithReadStatus(Long userId, int limit) {

        return noticeReadHistoryMapper.selectNoticeListWithReadStatus(userId, limit);
    }

    /**
     * 批量标记已读
     */
    @Override
    public void markReadBatch(Long userId, Long[] noticeIds) {

        if (noticeIds == null || noticeIds.length == 0) {

            return;
        }
        List<SysNoticeReadEntity> list = new java.util.ArrayList<>(noticeIds.length);
        for (Long noticeId : noticeIds) {

            SysNoticeReadEntity entity = new SysNoticeReadEntity();
            entity.setNoticeId(noticeId);
            entity.setUserId(userId);
            list.add(entity);
        }
        noticeReadMapper.insertNoticeReadBatch(list);
    }

    /**
     * 查询已阅读某公告的用户列表
     */
    @Override
    public List<Map<String, Object>> selectReadUsersByNoticeId(Long noticeId, String searchValue) {

        return noticeReadHistoryMapper.selectReadUsersByNoticeId(noticeId, searchValue);
    }

    /**
     * 删除公告时清理对应已读记录
     */
    @Override
    public void deleteByNoticeIds(Long[] noticeIds) {

        noticeReadMapper.deleteByNoticeIds(java.util.Arrays.asList(noticeIds));
    }
}
