package com.medcase.system.service;

import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.system.entity.SysNoticeReadEntity;
import com.medcase.system.mapper.SysNoticeReadMapper;
import com.medcase.web.controller.system.dto.NoticeReadUserResponse;
import com.medcase.web.controller.system.dto.NoticeTopItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 公告已读记录 服务层实现
 */
@Service
@RequiredArgsConstructor
public class SysNoticeReadService {

    private final SysNoticeReadMapper noticeReadMapper;

    private final SysDeptService deptService;

    /**
     * 标记已读
     */
    public void markRead(Long noticeId, Long userId) {
        SysNoticeReadEntity record = new SysNoticeReadEntity();
        record.setNoticeId(noticeId);
        record.setUserId(userId);
        record.setReadTime(new Date());
        noticeReadMapper.insert(record);
    }

    /**
     * 查询公告列表并标记当前用户已读状态
     */
    public List<NoticeTopItemResponse> selectNoticeListWithReadStatus(Long userId, int limit) {
        List<NoticeTopItemResponse> notices = noticeReadMapper.selectTopNoticeList(limit);
        if (notices.isEmpty()) {
            return notices;
        }

        List<Long> noticeIds = notices.stream()
                .map(NoticeTopItemResponse::getNoticeId)
                .collect(Collectors.toList());
        Set<Long> readNoticeIds = noticeReadMapper.selectReadNoticeIds(userId, noticeIds);
        notices.forEach(notice -> notice.setRead(readNoticeIds.contains(notice.getNoticeId())));
        return notices;
    }

    /**
     * 批量标记已读
     */
    public void markReadBatch(Long userId, Long[] noticeIds) {
        if (noticeIds == null || noticeIds.length == 0) {
            return;
        }
        List<SysNoticeReadEntity> list = new ArrayList<>(noticeIds.length);
        Date readTime = new Date();
        for (Long noticeId : noticeIds) {
            SysNoticeReadEntity entity = new SysNoticeReadEntity();
            entity.setNoticeId(noticeId);
            entity.setUserId(userId);
            entity.setReadTime(readTime);
            list.add(entity);
        }
        noticeReadMapper.insertNoticeReadBatch(list);
    }

    /**
     * 查询已阅读某公告的用户列表
     */
    public List<NoticeReadUserResponse> selectReadUsersByNoticeId(Long noticeId, String nickNameLike) {
        List<NoticeReadUserResponse> result =
                noticeReadMapper.selectReadUsersByNoticeId(noticeId, nickNameLike);
        for (NoticeReadUserResponse item : result) {
            SysDept dept = deptService.selectDeptById(item.getDeptId());
            if (dept != null) {
                item.setDeptName(dept.getDeptName());
            }
        }
        return result;
    }

    /**
     * 删除公告时清理对应已读记录
     */
    public void deleteByNoticeIds(Long[] noticeIds) {
        noticeReadMapper.deleteByNoticeIds(Arrays.asList(noticeIds));
    }
}
