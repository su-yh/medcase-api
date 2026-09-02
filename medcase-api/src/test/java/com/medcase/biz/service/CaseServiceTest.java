package com.medcase.biz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medcase.biz.domain.CaseEntity;
import com.medcase.biz.enums.CaseStatusEnums;
import com.medcase.biz.mapper.CaseMapper;
import com.medcase.biz.request.CaseSubmitRequest;
import com.medcase.biz.response.CaseVO;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.validation.groups.ValidationGroups;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.common.enums.UserTypeEnums;
import java.io.Serializable;
import java.util.Date;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CaseServiceTest {
    private CaseService caseService;

    private Validator validator;

    @Mock
    private CaseMapper caseMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        caseService = new CaseService(caseMapper);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void submitAllowsNewCaseWithoutId() {
        LoginUser loginUser = loginUser(12L);
        CaseSubmitRequest request = submitRequest(null);
        when(caseMapper.insert(any(CaseEntity.class))).thenReturn(1);

        caseService.submit(loginUser, request);

        ArgumentCaptor<CaseEntity> captor = ArgumentCaptor.forClass(CaseEntity.class);
        verify(caseMapper).insert(captor.capture());
        CaseEntity entity = captor.getValue();
        assertEquals(12L, entity.getUserId());
        assertEquals("doctor12", entity.getUserNickname());
        assertEquals("病例", entity.getCaseName());
        assertEquals("病例内容", entity.getContent());
        assertEquals(CaseStatusEnums.PENDING_REVIEW, entity.getStatus());
    }

    @Test
    void submitRejectsMissingDraftForProvidedId() {
        LoginUser loginUser = loginUser(12L);
        CaseSubmitRequest request = submitRequest(42L);
        when(caseMapper.selectById(42L)).thenReturn(null);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> caseService.submit(loginUser, request));

        assertEquals(ErrorCodeEnums.CASE_NOT_FOUND, exception.getEc());
        verify(caseMapper, never()).insert(any(CaseEntity.class));
        verify(caseMapper, never()).updateById(any(CaseEntity.class));
    }

    @Test
    void saveDraftRejectsMissingDraftForProvidedId() {
        LoginUser loginUser = loginUser(12L);
        CaseSubmitRequest request = submitRequest(42L);
        when(caseMapper.selectById(42L)).thenReturn(null);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> caseService.saveDraft(loginUser, request));

        assertEquals(ErrorCodeEnums.CASE_NOT_FOUND, exception.getEc());
        verify(caseMapper, never()).insert(any(CaseEntity.class));
        verify(caseMapper, never()).updateById(any(CaseEntity.class));
    }

    @Test
    void submitValidationAllowsNewCaseWithoutId() {
        CaseSubmitRequest request = submitRequest(null);

        assertTrue(validator.validate(
                request,
                ValidationGroups.Doctor.Submit.class).isEmpty());
    }

    @Test
    void deleteRemovesOwnedDraft() {
        LoginUser loginUser = loginUser(12L);
        CaseEntity entity = caseEntity(42L, 12L, CaseStatusEnums.DRAFT);
        when(caseMapper.selectById(42L)).thenReturn(entity);
        when(caseMapper.deleteById(42L)).thenReturn(1);

        caseService.delete(loginUser, 42L);

        verify(caseMapper).deleteById(42L);
    }

    @Test
    void deleteRejectsNonDraftCase() {
        LoginUser loginUser = loginUser(12L);
        CaseEntity entity = caseEntity(42L, 12L, CaseStatusEnums.PENDING_REVIEW);
        when(caseMapper.selectById(42L)).thenReturn(entity);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> caseService.delete(loginUser, 42L));

        assertEquals(ErrorCodeEnums.CASE_DELETE_STATUS_NOT_MATCH, exception.getEc());
        verify(caseMapper, never()).deleteById(any(Serializable.class));
    }

    @Test
    void deleteRejectsCaseOwnedByAnotherDoctor() {
        LoginUser loginUser = loginUser(12L);
        CaseEntity entity = caseEntity(42L, 99L, CaseStatusEnums.DRAFT);
        when(caseMapper.selectById(42L)).thenReturn(entity);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> caseService.delete(loginUser, 42L));

        assertEquals(ErrorCodeEnums.CASE_UPDATE_REJECT, exception.getEc());
        verify(caseMapper, never()).deleteById(any(Serializable.class));
    }

    @Test
    void deleteRejectsMissingCase() {
        LoginUser loginUser = loginUser(12L);
        when(caseMapper.selectById(42L)).thenReturn(null);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> caseService.delete(loginUser, 42L));

        assertEquals(ErrorCodeEnums.CASE_NOT_FOUND, exception.getEc());
        verify(caseMapper, never()).deleteById(any(Serializable.class));
    }

    @Test
    void deleteRejectsMapperFailure() {
        LoginUser loginUser = loginUser(12L);
        CaseEntity entity = caseEntity(42L, 12L, CaseStatusEnums.DRAFT);
        when(caseMapper.selectById(42L)).thenReturn(entity);
        when(caseMapper.deleteById(42L)).thenReturn(0);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> caseService.delete(loginUser, 42L));

        assertEquals(ErrorCodeEnums.CASE_DELETE_FAILED, exception.getEc());
    }

    @Test
    void caseViewIncludesReviewAndSettlementOperators() {
        CaseEntity entity = caseEntity(42L, 12L, CaseStatusEnums.SETTLED);
        Date reviewTime = new Date(1_000L);
        Date settledTime = new Date(2_000L);
        entity.setReviewerNickname("审核员");
        entity.setReviewTime(reviewTime);
        entity.setSettlerNickname("结算员");
        entity.setSettledTime(settledTime);

        CaseVO result = CaseVO.fromEntity(entity);

        assertEquals("审核员", result.getReviewerNickname());
        assertEquals(reviewTime, result.getReviewTime());
        assertEquals("结算员", result.getSettlerNickname());
        assertEquals(settledTime, result.getSettledTime());
    }

    private LoginUser loginUser(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setNickName("doctor" + userId);
        user.setUserType(UserTypeEnums.DOCTOR);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUser(user);
        return loginUser;
    }

    private CaseSubmitRequest submitRequest(Long id) {
        CaseSubmitRequest request = new CaseSubmitRequest();
        request.setId(id);
        request.setCaseName("病例");
        request.setContent("病例内容");
        return request;
    }

    private CaseEntity caseEntity(Long id, Long doctorId, CaseStatusEnums status) {
        CaseEntity entity = new CaseEntity();
        entity.setId(id);
        entity.setUserId(doctorId);
        entity.setUserType(UserTypeEnums.DOCTOR);
        entity.setStatus(status);
        return entity;
    }

}
