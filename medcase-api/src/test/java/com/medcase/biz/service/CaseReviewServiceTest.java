package com.medcase.biz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.biz.request.CaseReviewRequest;
import com.medcase.biz.request.CaseReviewQuery;
import com.medcase.biz.response.CaseReviewVO;
import com.medcase.biz.domain.CaseEntity;
import com.medcase.biz.enums.CaseStatusEnums;
import com.medcase.biz.mapper.CaseAdminMapper;
import com.medcase.common.enums.UserTypeEnums;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

class CaseReviewServiceTest {
    private CaseReviewService caseReviewService;

    @Mock
    private CaseAdminMapper caseAdminMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        caseReviewService = new CaseReviewService(caseAdminMapper);
    }

    @Test
    void pageUsesProvidedPageParametersAndMapsCaseFields() {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(2);
        pageParam.setPageSize(20);
        CaseReviewQuery query = new CaseReviewQuery();
        query.setId(42L);
        query.setCaseName("高血压");
        query.setStatus(CaseStatusEnums.PENDING_REVIEW);

        CaseEntity entity = new CaseEntity();
        entity.setId(42L);
        entity.setUserId(12L);
        entity.setUserNickname("张医生");
        entity.setUserType(UserTypeEnums.DOCTOR);
        entity.setCaseName("高血压病例");
        entity.setContent("病例内容");
        entity.setStatus(CaseStatusEnums.PENDING_REVIEW);
        when(caseAdminMapper.selectAdminCasePage(any(PageParam.class), any(CaseReviewQuery.class)))
                .thenReturn(new PageResult<>(List.of(entity), 1L));

        PageResult<CaseReviewVO> result =
                caseReviewService.page(pageParam, query, UserTypeEnums.DOCTOR);

        ArgumentCaptor<PageParam> pageCaptor = ArgumentCaptor.forClass(PageParam.class);
        verify(caseAdminMapper).selectAdminCasePage(pageCaptor.capture(), any(CaseReviewQuery.class));
        assertEquals(pageParam, pageCaptor.getValue());
        assertEquals(1, result.getTotal());
        assertEquals(42L, result.getList().get(0).getId());
        assertEquals("张医生", result.getList().get(0).getUserName());
        assertEquals("高血压病例", result.getList().get(0).getCaseName());
        assertEquals("病例内容", result.getList().get(0).getContent());
        assertEquals("待审核", result.getList().get(0).getStatusDesc());
    }

    @Test
    void reviewPendingCaseAsApproved() throws NoSuchMethodException {
        CaseEntity entity = caseEntity(42L, CaseStatusEnums.PENDING_REVIEW);
        LoginUser adminUser = loginUser(7L, "管理员");
        when(caseAdminMapper.selectById(42L)).thenReturn(entity);
        when(caseAdminMapper.updateById(entity)).thenReturn(1);

        caseReviewService.review(
                42L,
                reviewRequest(true, null),
                adminUser,
                UserTypeEnums.DOCTOR);

        verify(caseAdminMapper).updateById(entity);
        assertEquals(CaseStatusEnums.APPROVED_PENDING_SETTLEMENT, entity.getStatus());
        assertEquals("通过", entity.getReviewReason());
        assertEquals(7L, entity.getReviewerId());
        assertEquals("管理员", entity.getReviewerNickname());
        assertEquals(
                Transactional.class,
                CaseReviewService.class
                        .getMethod(
                                "review",
                                Long.class,
                                CaseReviewRequest.class,
                                LoginUser.class,
                                UserTypeEnums.class)
                        .getAnnotation(Transactional.class)
                        .annotationType());
    }

    @Test
    void reviewPendingCaseAsRejectedWithReason() {
        CaseEntity entity = caseEntity(42L, CaseStatusEnums.PENDING_REVIEW);
        LoginUser adminUser = loginUser(7L, "管理员");
        when(caseAdminMapper.selectById(42L)).thenReturn(entity);
        when(caseAdminMapper.updateById(entity)).thenReturn(1);

        caseReviewService.review(
                42L,
                reviewRequest(false, "请补充检查报告"),
                adminUser,
                UserTypeEnums.DOCTOR);

        verify(caseAdminMapper).updateById(entity);
        assertEquals(CaseStatusEnums.REVIEW_FAILED, entity.getStatus());
        assertEquals("请补充检查报告", entity.getReviewReason());
        assertEquals(7L, entity.getReviewerId());
        assertEquals("管理员", entity.getReviewerNickname());
    }

    @Test
    void reviewRejectsNonPendingCase() {
        CaseEntity entity = caseEntity(42L, CaseStatusEnums.DRAFT);
        when(caseAdminMapper.selectById(42L)).thenReturn(entity);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> caseReviewService.review(
                        42L,
                        reviewRequest(true, null),
                        loginUser(7L, "管理员"),
                        UserTypeEnums.DOCTOR));

        assertEquals(ErrorCodeEnums.CASE_REVIEW_STATUS_NOT_MATCH, exception.getEc());
        verify(caseAdminMapper, never())
                .updateById(any(CaseEntity.class));
    }

    @Test
    void rejectRequiresReason() {
        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> caseReviewService.review(
                        42L,
                        reviewRequest(false, "  "),
                        loginUser(7L, "管理员"),
                        UserTypeEnums.DOCTOR));

        assertEquals(ErrorCodeEnums.CASE_REVIEW_REASON_EMPTY, exception.getEc());
        verify(caseAdminMapper, never()).selectById(any());
    }

    @Test
    void settleApprovedPendingCase() {
        CaseEntity entity = caseEntity(42L, CaseStatusEnums.APPROVED_PENDING_SETTLEMENT);
        LoginUser adminUser = loginUser(7L, "管理员");
        when(caseAdminMapper.selectById(42L)).thenReturn(entity);
        when(caseAdminMapper.updateById(entity)).thenReturn(1);

        caseReviewService.settle(42L, adminUser, UserTypeEnums.DOCTOR);

        verify(caseAdminMapper).updateById(entity);
        assertEquals(CaseStatusEnums.SETTLED, entity.getStatus());
        assertEquals(7L, entity.getSettlerId());
        assertEquals("管理员", entity.getSettlerNickname());
    }

    @Test
    void settleRejectsCaseWithUnexpectedStatus() {
        CaseEntity entity = caseEntity(42L, CaseStatusEnums.PENDING_REVIEW);
        when(caseAdminMapper.selectById(42L)).thenReturn(entity);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> caseReviewService.settle(42L, loginUser(7L, "管理员"), UserTypeEnums.DOCTOR));

        assertEquals(ErrorCodeEnums.CASE_SETTLE_STATUS_NOT_MATCH, exception.getEc());
        verify(caseAdminMapper, never()).updateById(any(CaseEntity.class));
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

    private CaseReviewRequest reviewRequest(Boolean approve, String reason) {
        CaseReviewRequest request = new CaseReviewRequest();
        request.setApprove(approve);
        request.setReason(reason);
        return request;
    }

    private CaseEntity caseEntity(Long id, CaseStatusEnums status) {
        CaseEntity entity = new CaseEntity();
        entity.setId(id);
        entity.setStatus(status);
        entity.setUserType(UserTypeEnums.DOCTOR);
        return entity;
    }
}
