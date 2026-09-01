package com.medcase.system.service.impl;

import com.medcase.system.mapper.SysNoticeReadMapper;
import com.medcase.web.controller.system.dto.NoticeTopItemResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class SysNoticeReadServiceImplTest {
    private SysNoticeReadServiceImpl noticeReadService;

    @Mock
    private SysNoticeReadMapper noticeReadMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noticeReadService = new SysNoticeReadServiceImpl(noticeReadMapper);
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

    private NoticeTopItemResponse notice(Long noticeId) {
        NoticeTopItemResponse response = new NoticeTopItemResponse();
        response.setNoticeId(noticeId);
        return response;
    }
}
