package com.ruoyi.biz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.biz.mapper.DoctorUserMapper;
import com.ruoyi.biz.request.DoctorProfileSubmitRequest;
import com.ruoyi.biz.response.DoctorProfileVO;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatusEnums;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.AbstractBusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class DoctorProfileServiceTest {
    private DoctorProfileService doctorProfileService;

    @Mock
    private DoctorUserMapper doctorUserMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctorProfileService = new DoctorProfileService(doctorUserMapper);
    }

    @Test
    void meReturnsCurrentDoctorProfile() {
        DoctorUserEntity doctor = doctor(UserStatusEnums.REGISTER);
        doctor.setNickName("张医生");
        doctor.setPhonenumber("13800000000");
        when(doctorUserMapper.selectDoctorById(12L)).thenReturn(doctor);

        DoctorProfileVO result = doctorProfileService.me(loginUser());

        assertEquals(12L, result.getId());
        assertEquals("张医生", result.getName());
        assertEquals("13800000000", result.getPhone());
        assertEquals(UserStatusEnums.REGISTER, result.getStatus());
    }

    @Test
    void submitMovesRegisteredDoctorToPendingReview() {
        DoctorUserEntity doctor = doctor(UserStatusEnums.REGISTER);
        when(doctorUserMapper.selectDoctorById(12L)).thenReturn(doctor);
        when(doctorUserMapper.updateById(doctor)).thenReturn(1);
        DoctorProfileSubmitRequest request = request("张医生", "13800000000");

        doctorProfileService.submit(loginUser(), request);

        assertEquals("张医生", doctor.getNickName());
        assertEquals("13800000000", doctor.getPhonenumber());
        assertEquals(UserStatusEnums.PENDING_REVIEW, doctor.getStatus());
        verify(doctorUserMapper).updateById(doctor);
    }

    @Test
    void submitMovesReviewFailedDoctorBackToPendingReview() {
        DoctorUserEntity doctor = doctor(UserStatusEnums.REVIEW_FAILED);
        when(doctorUserMapper.selectDoctorById(12L)).thenReturn(doctor);
        when(doctorUserMapper.updateById(doctor)).thenReturn(1);

        doctorProfileService.submit(loginUser(), request("李医生", "13900000000"));

        assertEquals(UserStatusEnums.PENDING_REVIEW, doctor.getStatus());
        verify(doctorUserMapper).updateById(doctor);
    }

    @Test
    void submitRejectsDoctorOutsideProfileSubmissionStatuses() {
        DoctorUserEntity doctor = doctor(UserStatusEnums.OK);
        when(doctorUserMapper.selectDoctorById(12L)).thenReturn(doctor);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> doctorProfileService.submit(loginUser(), request("张医生", "13800000000")));

        assertEquals(ErrorCodeEnums.DOCTOR_PROFILE_SUBMIT_STATUS_NOT_MATCH, exception.getEc());
        verify(doctorUserMapper, never()).updateById(any(DoctorUserEntity.class));
    }

    private DoctorProfileSubmitRequest request(String name, String phone) {
        DoctorProfileSubmitRequest request = new DoctorProfileSubmitRequest();
        request.setName(name);
        request.setPhone(phone);
        return request;
    }

    private LoginUser loginUser() {
        SysUser user = new SysUser();
        user.setUserId(12L);
        user.setUserType(UserTypeEnums.DOCTOR);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(12L);
        loginUser.setUser(user);
        return loginUser;
    }

    private DoctorUserEntity doctor(UserStatusEnums status) {
        DoctorUserEntity doctor = new DoctorUserEntity();
        doctor.setUserId(12L);
        doctor.setUserType(UserTypeEnums.DOCTOR);
        doctor.setStatus(status);
        return doctor;
    }
}
