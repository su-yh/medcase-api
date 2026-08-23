package com.ruoyi.biz.caseinfo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.biz.caseinfo.request.DoctorCaseReviewQuery;
import com.ruoyi.biz.caseinfo.response.DoctorCaseReviewVO;
import com.ruoyi.biz.caseinfo.domain.DoctorCaseEntity;
import com.ruoyi.biz.caseinfo.enums.DoctorCaseStatusEnums;
import com.ruoyi.biz.caseinfo.mapper.DoctorCaseAdminMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DoctorCaseReviewServiceTest {
    private DoctorCaseReviewService doctorCaseReviewService;

    @Mock
    private DoctorCaseAdminMapper doctorCaseAdminMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorCaseReviewService = new DoctorCaseReviewService(doctorCaseAdminMapper);
    }

    @Test
    void pageBuildsAdminPageParametersAndMapsCaseFields() {
        DoctorCaseReviewQuery query = new DoctorCaseReviewQuery();
        query.setId(42L);
        query.setTitle("高血压");
        query.setStatus(DoctorCaseStatusEnums.PENDING_REVIEW);

        DoctorCaseEntity entity = new DoctorCaseEntity();
        entity.setId(42L);
        entity.setDoctorId(12L);
        entity.setDoctorNickname("张医生");
        entity.setTitle("高血压病例");
        entity.setStatus(DoctorCaseStatusEnums.PENDING_REVIEW);
        when(doctorCaseAdminMapper.selectAdminCasePage(any(PageParam.class), any(DoctorCaseReviewQuery.class)))
                .thenReturn(new PageResult<>(List.of(entity), 1L));

        PageResult<DoctorCaseReviewVO> result = doctorCaseReviewService.page(2, 20, query);

        ArgumentCaptor<PageParam> pageCaptor = ArgumentCaptor.forClass(PageParam.class);
        verify(doctorCaseAdminMapper).selectAdminCasePage(pageCaptor.capture(), any(DoctorCaseReviewQuery.class));
        assertEquals(2, pageCaptor.getValue().getPageNo());
        assertEquals(20, pageCaptor.getValue().getPageSize());
        assertEquals(1L, result.getTotal());
        assertEquals(42L, result.getList().get(0).getId());
        assertEquals("张医生", result.getList().get(0).getDoctorName());
        assertEquals("待审核", result.getList().get(0).getStatusDesc());
    }
}
