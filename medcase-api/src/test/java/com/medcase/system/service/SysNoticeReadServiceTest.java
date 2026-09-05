package com.medcase.system.service;

import com.medcase.system.entity.SysNoticeReadEntity;
import com.medcase.system.mapper.SysNoticeReadMapper;
import com.medcase.web.controller.system.dto.NoticeTopItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysNoticeReadServiceTest {
    private SysNoticeReadService noticeReadService;

    @Mock
    private SysNoticeReadMapper noticeReadMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeReadService = new SysNoticeReadService(noticeReadMapper);
    }

    @Test
    void selectNoticeListWithReadStatusMarksReadInService() {
        NoticeTopItemResponse readNotice = notice(1L);
        NoticeTopItemResponse unreadNotice = notice(2L);
        when(noticeReadMapper.selectTopNoticeList(5)).thenReturn(List.of(readNotice, unreadNotice));
        when(noticeReadMapper.selectReadNoticeIds(12L, List.of(1L, 2L))).thenReturn(Set.of(1L));

        List<NoticeTopItemResponse> result = noticeReadService.selectNoticeListWithReadStatus(12L, 5);

        assertTrue(result.get(0).isRead());
        assertFalse(result.get(1).isRead());
    }

    @Test
    void markReadSetsReadTimeBeforeInsert() {
        noticeReadService.markRead(1L, 12L);

        org.mockito.ArgumentCaptor<SysNoticeReadEntity> captor =
                org.mockito.ArgumentCaptor.forClass(SysNoticeReadEntity.class);
        verify(noticeReadMapper).insert(captor.capture());

        SysNoticeReadEntity record = captor.getValue();
        assertEquals(1L, record.getNoticeId());
        assertEquals(12L, record.getUserId());
        assertNotNull(record.getReadTime());
    }

    @Test
    void markReadBatchSetsReadTimeBeforeInsert() {
        noticeReadService.markReadBatch(12L, new Long[]{1L, 2L});

        org.mockito.ArgumentCaptor<Collection<SysNoticeReadEntity>> captor =
                org.mockito.ArgumentCaptor.forClass(Collection.class);
        verify(noticeReadMapper).insertNoticeReadBatch(captor.capture());

        Collection<SysNoticeReadEntity> records = captor.getValue();
        assertEquals(2, records.size());
        assertTrue(records.stream().allMatch(record -> record.getReadTime() != null));
    }

    private NoticeTopItemResponse notice(Long noticeId) {
        NoticeTopItemResponse response = new NoticeTopItemResponse();
        response.setNoticeId(noticeId);
        return response;
    }
}
