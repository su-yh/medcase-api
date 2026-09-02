package com.medcase.biz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.domain.SupplierEntity;
import com.medcase.biz.mapper.SupplierMapper;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.biz.request.UserProfileSubmitRequest;
import com.medcase.biz.response.UserProfileVO;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.storage.pojo.FileAttachment;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserProfileServiceTest {
    private UserProfileService userProfileService;

    private Validator validator;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SupplierMapper supplierMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        userProfileService = new UserProfileService(userMapper, supplierMapper, validator);
        when(supplierMapper.selectEnabledById(1L)).thenReturn(enabledSupplier());
    }

    @Test
    void meReturnsCurrentUserProfile() {
        UserEntity doctor = doctor(UserStatusEnums.REGISTER);
        doctor.setNickName("张医生");
        doctor.setSex("1");
        doctor.setPhonenumber("13800000000");
        doctor.setIdCardNumber("110101199001011234");
        doctor.setTitle("主治医师");
        doctor.setIdCardFront(attachment("front"));
        doctor.setIdCardBack(attachment("back"));
        doctor.setQualificationCertificate(attachment("qualification"));
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);

        UserProfileVO result = userProfileService.me(loginUser());

        assertEquals(12L, result.getId());
        assertEquals("张医生", result.getNickName());
        assertEquals("1", result.getSex());
        assertEquals("13800000000", result.getPhone());
        assertEquals("110101199001011234", result.getIdCardNumber());
        assertEquals("主治医师", result.getTitle());
        assertEquals("front", result.getIdCardFront().getOriginalFilename());
        assertEquals(UserStatusEnums.REGISTER, result.getStatus());
    }

    @Test
    void submitMovesRegisteredDoctorToPendingReview() {
        UserEntity doctor = doctor(UserStatusEnums.REGISTER);
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(userMapper.updateById(doctor)).thenReturn(1);
        UserProfileSubmitRequest request = request("张医生", "13800000000");
        userProfileService.submit(loginUser(), request);

        assertEquals("张医生", doctor.getNickName());
        assertEquals("13800000000", doctor.getPhonenumber());
        assertEquals(1L, doctor.getSupplierId());
        assertEquals(UserStatusEnums.PENDING_REVIEW, doctor.getStatus());
        verify(userMapper).updateById(doctor);
    }

    @Test
    void submitMovesReviewFailedDoctorBackToPendingReview() {
        UserEntity doctor = doctor(UserStatusEnums.REVIEW_FAILED);
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(userMapper.updateById(doctor)).thenReturn(1);

        userProfileService.submit(loginUser(), request("李医生", "13900000000"));

        assertEquals(UserStatusEnums.PENDING_REVIEW, doctor.getStatus());
        verify(userMapper).updateById(doctor);
    }

    @Test
    void submitRejectsDoctorOutsideProfileSubmissionStatuses() {
        UserEntity doctor = doctor(UserStatusEnums.OK);
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> userProfileService.submit(loginUser(), request("张医生", "13800000000")));

        assertEquals(ErrorCodeEnums.USER_PROFILE_SUBMIT_STATUS_NOT_MATCH, exception.getEc());
        verify(userMapper, never()).updateById(any(UserEntity.class));
    }

    @Test
    void submitRejectsDoctorMissingQualificationByValidation() {
        UserEntity doctor = doctor(UserStatusEnums.REGISTER);
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        UserProfileSubmitRequest request = request("张医生", "13800000000");
        request.setQualificationCertificate(null);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> userProfileService.submit(loginUser(), request));

        assertEquals("医师职业资格证图片不能为空",
                exception.getConstraintViolations().iterator().next().getMessage());
        verify(userMapper, never()).updateById(any(UserEntity.class));
    }

    @Test
    void submitPatientDoesNotRequireDoctorQualification() {
        UserEntity patient = doctor(UserStatusEnums.REGISTER);
        patient.setUserType(UserTypeEnums.PATIENT);
        LoginUser patientUser = loginUser(UserTypeEnums.PATIENT);
        when(userMapper.selectUserById(12L, UserTypeEnums.PATIENT)).thenReturn(patient);
        when(userMapper.updateById(patient)).thenReturn(1);
        UserProfileSubmitRequest request = request("张患者", "13800000000");
        request.setTitle(null);
        request.setQualificationCertificate(null);

        userProfileService.submit(patientUser, request);

        assertEquals(null, patient.getTitle());
        assertEquals(null, patient.getQualificationCertificate());
        verify(userMapper).updateById(patient);
    }

    private UserProfileSubmitRequest request(String nickName, String phone) {
        UserProfileSubmitRequest request = new UserProfileSubmitRequest();
        request.setNickName(nickName);
        request.setPhone(phone);
        request.setIdCardNumber("110101199001011234");
        request.setSupplierId(1L);
        request.setTitle("主治医师");
        request.setIdCardFront(attachment("front"));
        request.setIdCardBack(attachment("back"));
        request.setQualificationCertificate(attachment("qualification"));
        return request;
    }

    private FileAttachment attachment(String filename) {
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("doctor/" + filename);
        attachment.setOriginalFilename(filename);
        return attachment;
    }

    private LoginUser loginUser() {
        return loginUser(UserTypeEnums.DOCTOR);
    }

    private LoginUser loginUser(UserTypeEnums userType) {
        SysUser user = new SysUser();
        user.setUserId(12L);
        user.setUserType(userType);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(12L);
        loginUser.setUser(user);
        return loginUser;
    }

    private UserEntity doctor(UserStatusEnums status) {
        UserEntity doctor = new UserEntity();
        doctor.setUserId(12L);
        doctor.setUserType(UserTypeEnums.DOCTOR);
        doctor.setStatus(status);
        return doctor;
    }

    private SupplierEntity enabledSupplier() {
        SupplierEntity supplier = new SupplierEntity();
        supplier.setId(1L);
        supplier.setStatus("0");
        return supplier;
    }
}
