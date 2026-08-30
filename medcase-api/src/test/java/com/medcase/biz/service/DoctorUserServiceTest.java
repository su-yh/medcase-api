package com.medcase.biz.service;

import com.medcase.biz.domain.DoctorUserEntity;
import com.medcase.biz.mapper.DoctorUserMapper;
import com.medcase.biz.request.DoctorUserReviewRequest;
import com.medcase.biz.request.DoctorUserQuery;
import com.medcase.biz.response.DoctorUserVO;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.storage.pojo.FileAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoctorUserServiceTest {
    private DoctorUserService doctorUserService;

    @Mock
    private DoctorUserMapper doctorUserMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorUserService = new DoctorUserService(doctorUserMapper);
    }

    @Test
    void doctorUserModelExposesReviewReason() {
        assertDoesNotThrow(() -> DoctorUserEntity.class.getDeclaredField("reviewReason"));
        assertDoesNotThrow(() -> DoctorUserVO.class.getDeclaredField("reviewReason"));
    }

    @Test
    void pagePassesQueryToMapperAndMapsDoctorFields() {
        DoctorUserQuery query = new DoctorUserQuery();
        query.setNickName("张医生");
        query.setPhone("13800000000");
        query.setStatus("0");

        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setNickName("张医生");
        user.setUserName("doctor01");
        user.setPhonenumber("13800000000");
        user.setIdCardNumber("110101199001011234");
        user.setTitle("主治医师");
        user.setIdCardFront(attachment("front"));
        user.setIdCardBack(attachment("back"));
        user.setQualificationCertificate(attachment("qualification"));
        user.setStatus(UserStatusEnums.OK);
        when(doctorUserMapper.selectDoctorPage(any(PageParam.class), org.mockito.ArgumentMatchers.same(query)))
                .thenReturn(new PageResult<>(List.of(user), 1L));

        PageResult<DoctorUserVO> result = doctorUserService.page(new PageParam(), query);

        verify(doctorUserMapper).selectDoctorPage(
                any(PageParam.class), org.mockito.ArgumentMatchers.same(query));
        assertEquals(1L, result.getTotal());
        assertEquals(1L, result.getList().get(0).getId());
        assertEquals("张医生", result.getList().get(0).getNickName());
        assertEquals("110101199001011234", result.getList().get(0).getIdCardNumber());
        assertEquals("主治医师", result.getList().get(0).getTitle());
    }

    @Test
    void detailRejectsNonDoctorUser() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.ADMIN);
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);

        assertNull(doctorUserService.detail(1L));
    }

    @Test
    void detailMapsDoctorAttachments() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setNickName("张医生");
        user.setIdCardNumber("110101199001011234");
        user.setTitle("主治医师");
        user.setIdCardFront(attachment("front"));
        user.setIdCardBack(attachment("back"));
        user.setQualificationCertificate(attachment("qualification"));
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);

        DoctorUserVO result = doctorUserService.detail(1L);

        assertEquals("front", result.getIdCardFront().getOriginalFilename());
        assertEquals("qualification", result.getQualificationCertificate().getOriginalFilename());
    }

    @Test
    void reviewApprovesPendingDoctor() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);
        when(doctorUserMapper.updateById(user)).thenReturn(1);

        DoctorUserReviewRequest request = new DoctorUserReviewRequest();
        request.setApprove(true);

        doctorUserService.review(1L, request);

        assertEquals(UserStatusEnums.OK, user.getStatus());
        verify(doctorUserMapper).updateById(user);
    }

    @Test
    void reviewApprovesRegisteredDoctor() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.REGISTER);
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);
        when(doctorUserMapper.updateById(user)).thenReturn(1);

        DoctorUserReviewRequest request = new DoctorUserReviewRequest();
        request.setApprove(true);

        doctorUserService.review(1L, request);

        assertEquals(UserStatusEnums.OK, user.getStatus());
        verify(doctorUserMapper).updateById(user);
    }

    @Test
    void reviewRejectsRegisteredDoctor() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.REGISTER);
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);
        when(doctorUserMapper.updateById(user)).thenReturn(1);

        DoctorUserReviewRequest request = new DoctorUserReviewRequest();
        request.setApprove(false);
        request.setReason("身份证照片不清晰");

        doctorUserService.review(1L, request);

        assertEquals(UserStatusEnums.REVIEW_FAILED, user.getStatus());
        assertEquals("身份证照片不清晰", user.getReviewReason());
        verify(doctorUserMapper).updateById(user);
        verify(doctorUserMapper, never()).phoneExists(any());
    }

    @Test
    void reviewRejectsWithoutReason() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);

        DoctorUserReviewRequest request = new DoctorUserReviewRequest();
        request.setApprove(false);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorUserService.review(1L, request));

        assertEquals(ErrorCodeEnums.DOCTOR_USER_REVIEW_REASON_EMPTY, exception.getEc());
        assertEquals(UserStatusEnums.PENDING_REVIEW, user.getStatus());
        verify(doctorUserMapper, never()).updateById(any(DoctorUserEntity.class));
    }

    @Test
    void reviewDoesNotCheckPhoneDuplicate() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);
        when(doctorUserMapper.updateById(user)).thenReturn(1);

        DoctorUserReviewRequest request = new DoctorUserReviewRequest();
        request.setApprove(true);

        doctorUserService.review(1L, request);

        assertEquals(UserStatusEnums.OK, user.getStatus());
        verify(doctorUserMapper, never()).phoneExists(any());
        verify(doctorUserMapper).updateById(user);
    }

    @Test
    void reviewRejectsPendingDoctorWithReviewFailedStatus() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);
        when(doctorUserMapper.updateById(user)).thenReturn(1);

        DoctorUserReviewRequest request = new DoctorUserReviewRequest();
        request.setApprove(false);
        request.setReason("资格证信息不完整");

        doctorUserService.review(1L, request);

        assertEquals(UserStatusEnums.REVIEW_FAILED, user.getStatus());
        assertEquals("资格证信息不完整", user.getReviewReason());
        verify(doctorUserMapper).updateById(user);
    }

    @Test
    void reviewApprovesDoctorAndClearsPreviousReviewReason() {
        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setUserType(UserTypeEnums.DOCTOR);
        user.setStatus(UserStatusEnums.PENDING_REVIEW);
        user.setReviewReason("历史拒绝原因");
        when(doctorUserMapper.selectDoctorById(1L)).thenReturn(user);
        when(doctorUserMapper.updateById(user)).thenReturn(1);

        DoctorUserReviewRequest request = new DoctorUserReviewRequest();
        request.setApprove(true);

        doctorUserService.review(1L, request);

        assertEquals(UserStatusEnums.OK, user.getStatus());
        assertNull(user.getReviewReason());
        verify(doctorUserMapper).updateById(user);
    }

    private FileAttachment attachment(String filename) {
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("doctor/" + filename);
        attachment.setOriginalFilename(filename);
        return attachment;
    }
}
