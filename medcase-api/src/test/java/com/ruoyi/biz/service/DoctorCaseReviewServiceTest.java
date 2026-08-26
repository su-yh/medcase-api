package com.ruoyi.biz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.AbstractBusinessException;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.biz.request.DoctorCaseReviewRequest;
import com.ruoyi.biz.request.DoctorCaseReviewQuery;
import com.ruoyi.biz.response.DoctorCaseReviewVO;
import com.ruoyi.biz.domain.DoctorCaseEntity;
import com.ruoyi.biz.enums.DoctorCaseStatusEnums;
import com.ruoyi.biz.mapper.DoctorCaseAdminMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

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

    @Test
    void reviewPendingCaseAsApproved() throws NoSuchMethodException {
        DoctorCaseEntity entity = caseEntity(42L, DoctorCaseStatusEnums.PENDING_REVIEW);
        LoginUser adminUser = loginUser(7L, "管理员");
        when(doctorCaseAdminMapper.selectById(42L)).thenReturn(entity);
        when(doctorCaseAdminMapper.updateById(entity)).thenReturn(1);

        doctorCaseReviewService.review(
                42L,
                reviewRequest(true, null),
                adminUser);

        verify(doctorCaseAdminMapper).updateById(entity);
        assertEquals(DoctorCaseStatusEnums.APPROVED_PENDING_SETTLEMENT, entity.getStatus());
        assertEquals("通过", entity.getReviewReason());
        assertEquals(7L, entity.getReviewerId());
        assertEquals("管理员", entity.getReviewerNickname());
        assertEquals(
                Transactional.class,
                DoctorCaseReviewService.class
                        .getMethod(
                                "review",
                                Long.class,
                                DoctorCaseReviewRequest.class,
                                LoginUser.class)
                        .getAnnotation(Transactional.class)
                        .annotationType());
    }

    @Test
    void reviewPendingCaseAsRejectedWithReason() {
        DoctorCaseEntity entity = caseEntity(42L, DoctorCaseStatusEnums.PENDING_REVIEW);
        LoginUser adminUser = loginUser(7L, "管理员");
        when(doctorCaseAdminMapper.selectById(42L)).thenReturn(entity);
        when(doctorCaseAdminMapper.updateById(entity)).thenReturn(1);

        doctorCaseReviewService.review(
                42L,
                reviewRequest(false, "请补充检查报告"),
                adminUser);

        verify(doctorCaseAdminMapper).updateById(entity);
        assertEquals(DoctorCaseStatusEnums.REVIEW_FAILED, entity.getStatus());
        assertEquals("请补充检查报告", entity.getReviewReason());
        assertEquals(7L, entity.getReviewerId());
        assertEquals("管理员", entity.getReviewerNickname());
    }

    @Test
    void reviewRejectsNonPendingCase() {
        DoctorCaseEntity entity = caseEntity(42L, DoctorCaseStatusEnums.DRAFT);
        when(doctorCaseAdminMapper.selectById(42L)).thenReturn(entity);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorCaseReviewService.review(
                        42L,
                        reviewRequest(true, null),
                        loginUser(7L, "管理员")));

        assertEquals(ErrorCodeEnums.DOCTOR_CASE_REVIEW_STATUS_NOT_MATCH, exception.getEc());
        verify(doctorCaseAdminMapper, never())
                .updateById(any(DoctorCaseEntity.class));
    }

    @Test
    void rejectRequiresReason() {
        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorCaseReviewService.review(
                        42L,
                        reviewRequest(false, "  "),
                        loginUser(7L, "管理员")));

        assertEquals(ErrorCodeEnums.DOCTOR_CASE_REVIEW_REASON_EMPTY, exception.getEc());
        verify(doctorCaseAdminMapper, never()).selectById(any());
    }

    @Test
    void settleApprovedPendingCase() {
        DoctorCaseEntity entity = caseEntity(42L, DoctorCaseStatusEnums.APPROVED_PENDING_SETTLEMENT);
        LoginUser adminUser = loginUser(7L, "管理员");
        when(doctorCaseAdminMapper.selectById(42L)).thenReturn(entity);
        when(doctorCaseAdminMapper.updateById(entity)).thenReturn(1);

        doctorCaseReviewService.settle(42L, adminUser);

        verify(doctorCaseAdminMapper).updateById(entity);
        assertEquals(DoctorCaseStatusEnums.SETTLED, entity.getStatus());
        assertEquals(7L, entity.getSettlerId());
        assertEquals("管理员", entity.getSettlerNickname());
    }

    @Test
    void settleRejectsCaseWithUnexpectedStatus() {
        DoctorCaseEntity entity = caseEntity(42L, DoctorCaseStatusEnums.PENDING_REVIEW);
        when(doctorCaseAdminMapper.selectById(42L)).thenReturn(entity);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorCaseReviewService.settle(42L, loginUser(7L, "管理员")));

        assertEquals(ErrorCodeEnums.DOCTOR_CASE_SETTLE_STATUS_NOT_MATCH, exception.getEc());
        verify(doctorCaseAdminMapper, never()).updateById(any(DoctorCaseEntity.class));
    }

    private LoginUser loginUser(Long userId, String nickname) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setNickName(nickname);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUser(user);
        return loginUser;
    }

    private DoctorCaseReviewRequest reviewRequest(Boolean approve, String reason) {
        DoctorCaseReviewRequest request = new DoctorCaseReviewRequest();
        request.setApprove(approve);
        request.setReason(reason);
        return request;
    }

    private DoctorCaseEntity caseEntity(Long id, DoctorCaseStatusEnums status) {
        DoctorCaseEntity entity = new DoctorCaseEntity();
        entity.setId(id);
        entity.setStatus(status);
        return entity;
    }
}
