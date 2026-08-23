package com.ruoyi.biz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.validation.groups.ValidationGroups;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.AbstractBusinessException;
import com.ruoyi.biz.request.DoctorCaseSubmitRequest;
import com.ruoyi.biz.domain.DoctorCaseEntity;
import com.ruoyi.biz.enums.DoctorCaseStatusEnums;
import com.ruoyi.biz.mapper.DoctorCaseMapper;
import com.ruoyi.biz.response.DoctorCaseVO;
import java.io.Serializable;
import java.util.Date;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DoctorCaseServiceTest {
    private DoctorCaseService doctorCaseService;

    private Validator validator;

    @Mock
    private DoctorCaseMapper doctorCaseMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorCaseService = new DoctorCaseService(doctorCaseMapper);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void submitAllowsNewCaseWithoutId() {
        LoginUser loginUser = loginUser(12L);
        DoctorCaseSubmitRequest request = submitRequest(null);
        when(doctorCaseMapper.insert(any(DoctorCaseEntity.class))).thenReturn(1);

        doctorCaseService.submit(loginUser, request);

        ArgumentCaptor<DoctorCaseEntity> captor = ArgumentCaptor.forClass(DoctorCaseEntity.class);
        verify(doctorCaseMapper).insert(captor.capture());
        DoctorCaseEntity entity = captor.getValue();
        assertEquals(12L, entity.getDoctorId());
        assertEquals("doctor12", entity.getDoctorNickname());
        assertEquals(DoctorCaseStatusEnums.PENDING_REVIEW, entity.getStatus());
    }

    @Test
    void submitRejectsMissingDraftForProvidedId() {
        LoginUser loginUser = loginUser(12L);
        DoctorCaseSubmitRequest request = submitRequest(42L);
        when(doctorCaseMapper.selectById(42L)).thenReturn(null);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorCaseService.submit(loginUser, request));

        assertEquals(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND, exception.getEc());
        verify(doctorCaseMapper, never()).insert(any(DoctorCaseEntity.class));
        verify(doctorCaseMapper, never()).updateById(any(DoctorCaseEntity.class));
    }

    @Test
    void saveDraftRejectsMissingDraftForProvidedId() {
        LoginUser loginUser = loginUser(12L);
        DoctorCaseSubmitRequest request = submitRequest(42L);
        when(doctorCaseMapper.selectById(42L)).thenReturn(null);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorCaseService.saveDraft(loginUser, request));

        assertEquals(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND, exception.getEc());
        verify(doctorCaseMapper, never()).insert(any(DoctorCaseEntity.class));
        verify(doctorCaseMapper, never()).updateById(any(DoctorCaseEntity.class));
    }

    @Test
    void submitValidationAllowsNewCaseWithoutId() {
        DoctorCaseSubmitRequest request = submitRequest(null);

        assertTrue(validator.validate(
                request,
                ValidationGroups.Doctor.Submit.class).isEmpty());
    }

    @Test
    void deleteRemovesOwnedDraft() {
        LoginUser loginUser = loginUser(12L);
        DoctorCaseEntity entity = caseEntity(42L, 12L, DoctorCaseStatusEnums.DRAFT);
        when(doctorCaseMapper.selectById(42L)).thenReturn(entity);
        when(doctorCaseMapper.deleteById(42L)).thenReturn(1);

        doctorCaseService.delete(loginUser, 42L);

        verify(doctorCaseMapper).deleteById(42L);
    }

    @Test
    void deleteRejectsNonDraftCase() {
        LoginUser loginUser = loginUser(12L);
        DoctorCaseEntity entity = caseEntity(42L, 12L, DoctorCaseStatusEnums.PENDING_REVIEW);
        when(doctorCaseMapper.selectById(42L)).thenReturn(entity);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorCaseService.delete(loginUser, 42L));

        assertEquals(ErrorCodeEnums.DOCTOR_DELETE_STATUS_NOT_MATCH, exception.getEc());
        verify(doctorCaseMapper, never()).deleteById(any(Serializable.class));
    }

    @Test
    void deleteRejectsCaseOwnedByAnotherDoctor() {
        LoginUser loginUser = loginUser(12L);
        DoctorCaseEntity entity = caseEntity(42L, 99L, DoctorCaseStatusEnums.DRAFT);
        when(doctorCaseMapper.selectById(42L)).thenReturn(entity);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorCaseService.delete(loginUser, 42L));

        assertEquals(ErrorCodeEnums.DOCTOR_UPDATE_REJECT, exception.getEc());
        verify(doctorCaseMapper, never()).deleteById(any(Serializable.class));
    }

    @Test
    void deleteRejectsMissingCase() {
        LoginUser loginUser = loginUser(12L);
        when(doctorCaseMapper.selectById(42L)).thenReturn(null);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorCaseService.delete(loginUser, 42L));

        assertEquals(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND, exception.getEc());
        verify(doctorCaseMapper, never()).deleteById(any(Serializable.class));
    }

    @Test
    void deleteRejectsMapperFailure() {
        LoginUser loginUser = loginUser(12L);
        DoctorCaseEntity entity = caseEntity(42L, 12L, DoctorCaseStatusEnums.DRAFT);
        when(doctorCaseMapper.selectById(42L)).thenReturn(entity);
        when(doctorCaseMapper.deleteById(42L)).thenReturn(0);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorCaseService.delete(loginUser, 42L));

        assertEquals(ErrorCodeEnums.DOCTOR_CASE_DELETE_FAILED, exception.getEc());
    }

    @Test
    void caseViewIncludesReviewAndSettlementOperators() {
        DoctorCaseEntity entity = caseEntity(42L, 12L, DoctorCaseStatusEnums.SETTLED);
        Date reviewTime = new Date(1_000L);
        Date settledTime = new Date(2_000L);
        entity.setReviewerNickname("审核员");
        entity.setReviewTime(reviewTime);
        entity.setSettlerNickname("结算员");
        entity.setSettledTime(settledTime);

        DoctorCaseVO result = DoctorCaseVO.fromEntity(entity);

        assertEquals("审核员", result.getReviewerNickname());
        assertEquals(reviewTime, result.getReviewTime());
        assertEquals("结算员", result.getSettlerNickname());
        assertEquals(settledTime, result.getSettledTime());
    }

    private LoginUser loginUser(Long userId) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setNickName("doctor" + userId);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(userId);
        loginUser.setUser(user);
        return loginUser;
    }

    private DoctorCaseSubmitRequest submitRequest(Long id) {
        DoctorCaseSubmitRequest request = new DoctorCaseSubmitRequest();
        request.setId(id);
        request.setTitle("病例");
        return request;
    }

    private DoctorCaseEntity caseEntity(Long id, Long doctorId, DoctorCaseStatusEnums status) {
        DoctorCaseEntity entity = new DoctorCaseEntity();
        entity.setId(id);
        entity.setDoctorId(doctorId);
        entity.setStatus(status);
        return entity;
    }
}
