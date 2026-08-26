package com.ruoyi.biz.service;

import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.biz.mapper.DoctorUserMapper;
import com.ruoyi.biz.request.DoctorUserReviewRequest;
import com.ruoyi.biz.request.DoctorUserQuery;
import com.ruoyi.biz.response.DoctorUserVO;
import com.ruoyi.common.enums.UserStatusEnums;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
    void pagePassesQueryToMapperAndMapsDoctorFields() {
        DoctorUserQuery query = new DoctorUserQuery();
        query.setName("张医生");
        query.setPhone("13800000000");
        query.setStatus("0");

        DoctorUserEntity user = new DoctorUserEntity();
        user.setUserId(1L);
        user.setNickName("张医生");
        user.setUserName("doctor01");
        user.setPhonenumber("13800000000");
        user.setStatus(UserStatusEnums.OK);
        when(doctorUserMapper.selectDoctorPage(any(PageParam.class), org.mockito.ArgumentMatchers.same(query)))
                .thenReturn(new PageResult<>(List.of(user), 1L));

        PageResult<DoctorUserVO> result = doctorUserService.page(new PageParam(), query);

        verify(doctorUserMapper).selectDoctorPage(
                any(PageParam.class), org.mockito.ArgumentMatchers.same(query));
        assertEquals(1L, result.getTotal());
        assertEquals(1L, result.getList().get(0).getId());
        assertEquals("张医生", result.getList().get(0).getName());
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

        doctorUserService.review(1L, request);

        assertEquals(UserStatusEnums.REVIEW_FAILED, user.getStatus());
        verify(doctorUserMapper).updateById(user);
        verify(doctorUserMapper, never()).phoneExists(any());
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

        doctorUserService.review(1L, request);

        assertEquals(UserStatusEnums.REVIEW_FAILED, user.getStatus());
        verify(doctorUserMapper).updateById(user);
    }
}
