package com.medcase.biz.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medcase.biz.domain.SupplierEntity;
import com.medcase.biz.mapper.SupplierMapper;
import com.medcase.biz.request.UserProfilePasswordRequest;
import com.medcase.biz.request.UserProfilePhoneRequest;
import com.medcase.biz.request.UserProfileSubmitRequest;
import com.medcase.biz.response.UserProfileVO;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.mapper.SysUserMapper;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserSexEnums;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.storage.pojo.FileAttachment;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Set;

class UserProfileServiceTest {
    private UserProfileService userProfileService;

    private Validator validator;

    @Mock
    private SysUserMapper userMapper;

    @Mock
    private SupplierMapper supplierMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRegisterSmsCodeService smsCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        userProfileService = new UserProfileService(
                userMapper, supplierMapper, validator, passwordEncoder, smsCodeService);
        when(supplierMapper.selectEnabledById(1L)).thenReturn(enabledSupplier());
    }

    @Test
    void meReturnsCurrentUserProfile() {
        SysUserEntity doctor = doctor(UserStatusEnums.REGISTER);
        doctor.setNickName("张医生");
        doctor.setSex(UserSexEnums.FEMALE);
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
        assertEquals(UserSexEnums.FEMALE, result.getSex());
        assertEquals("13800000000", result.getPhone());
        assertEquals("110101199001011234", result.getIdCardNumber());
        assertEquals("主治医师", result.getTitle());
        assertEquals("front", result.getIdCardFront().getOriginalFilename());
        assertEquals(UserStatusEnums.REGISTER, result.getStatus());
    }

    @Test
    void submitMovesRegisteredDoctorToPendingReview() {
        SysUserEntity doctor = doctor(UserStatusEnums.REGISTER);
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
        SysUserEntity doctor = doctor(UserStatusEnums.REVIEW_FAILED);
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(userMapper.updateById(doctor)).thenReturn(1);

        userProfileService.submit(loginUser(), request("李医生", "13900000000"));

        assertEquals(UserStatusEnums.PENDING_REVIEW, doctor.getStatus());
        verify(userMapper).updateById(doctor);
    }

    @Test
    void submitAllowsChangingExistingNicknameAndPhoneAfterReviewFailed() {
        SysUserEntity doctor = doctor(UserStatusEnums.REVIEW_FAILED);
        doctor.setNickName("原姓名");
        doctor.setPhonenumber("13800000000");
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(userMapper.updateById(doctor)).thenReturn(1);

        userProfileService.submit(loginUser(), request("新姓名", "13900000000"));

        assertEquals("新姓名", doctor.getNickName());
        assertEquals("13900000000", doctor.getPhonenumber());
        verify(userMapper).updateById(doctor);
    }

    @Test
    void submitRejectsDoctorOutsideProfileSubmissionStatuses() {
        SysUserEntity doctor = doctor(UserStatusEnums.OK);
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> userProfileService.submit(loginUser(), request("张医生", "13800000000")));

        assertEquals(ErrorCodeEnums.USER_PROFILE_SUBMIT_STATUS_NOT_MATCH, exception.getEc());
        verify(userMapper, never()).updateById(any(SysUserEntity.class));
    }

    @Test
    void submitRejectsDoctorMissingQualificationByValidation() {
        SysUserEntity doctor = doctor(UserStatusEnums.REGISTER);
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        UserProfileSubmitRequest request = request("张医生", "13800000000");
        request.setQualificationCertificate(null);

        ConstraintViolationException exception = assertThrows(
                ConstraintViolationException.class,
                () -> userProfileService.submit(loginUser(), request));

        assertEquals("医师职业资格证图片不能为空",
                exception.getConstraintViolations().iterator().next().getMessage());
        verify(userMapper, never()).updateById(any(SysUserEntity.class));
    }

    @Test
    void submitPatientDoesNotRequireDoctorQualification() {
        SysUserEntity patient = doctor(UserStatusEnums.REGISTER);
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

    @Test
    void updatePhoneChangesOnlyPhoneForCurrentUser() {
        SysUserEntity doctor = doctor(UserStatusEnums.OK);
        doctor.setPhonenumber("13800000000");
        doctor.setPassword("old-password-hash");
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(userMapper.phoneExists("13900000000", UserTypeEnums.DOCTOR)).thenReturn(false);
        when(userMapper.updateById(doctor)).thenReturn(1);
        when(passwordEncoder.matches("current-password", "old-password-hash")).thenReturn(true);

        UserProfilePhoneRequest request = new UserProfilePhoneRequest();
        request.setPhone("13900000000");
        request.setPassword("current-password");
        request.setSmsCode("123456");

        userProfileService.updatePhone(loginUser(), request);

        assertEquals("13900000000", doctor.getPhonenumber());
        verify(smsCodeService).verifyCode("13900000000", "123456");
        verify(userMapper).updateById(doctor);
    }

    @Test
    void updatePhoneRejectsInvalidCurrentPassword() {
        SysUserEntity doctor = doctor(UserStatusEnums.OK);
        doctor.setPassword("old-password-hash");
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(passwordEncoder.matches("wrong-password", "old-password-hash")).thenReturn(false);

        UserProfilePhoneRequest request = phoneRequest("13900000000", "wrong-password", "123456");

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> userProfileService.updatePhone(loginUser(), request));

        assertEquals(ErrorCodeEnums.PROFILE_OLD_PASSWORD_INVALID, exception.getEc());
        verify(smsCodeService, never()).verifyCode(any(), any());
        verify(userMapper, never()).updateById(any(SysUserEntity.class));
    }

    @Test
    void updatePhoneRejectsInvalidSmsCode() {
        SysUserEntity doctor = doctor(UserStatusEnums.OK);
        doctor.setPassword("old-password-hash");
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(passwordEncoder.matches("current-password", "old-password-hash")).thenReturn(true);
        doThrow(ExceptionUtil.business(ErrorCodeEnums.USER_REGISTER_SMS_CODE_INVALID))
                .when(smsCodeService).verifyCode("13900000000", "wrong-code");

        UserProfilePhoneRequest request = phoneRequest("13900000000", "current-password", "wrong-code");

        assertThrows(
                AbstractBusinessException.class,
                () -> userProfileService.updatePhone(loginUser(), request));

        verify(userMapper, never()).phoneExists(any(), any());
        verify(userMapper, never()).updateById(any(SysUserEntity.class));
    }

    @Test
    void updatePhoneRejectsDuplicatePhoneAfterPasswordAndSmsVerification() {
        SysUserEntity doctor = doctor(UserStatusEnums.OK);
        doctor.setPassword("old-password-hash");
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(userMapper.phoneExists("13900000000", UserTypeEnums.DOCTOR)).thenReturn(true);
        when(passwordEncoder.matches("current-password", "old-password-hash")).thenReturn(true);

        UserProfilePhoneRequest request = phoneRequest("13900000000", "current-password", "123456");

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class,
                () -> userProfileService.updatePhone(loginUser(), request));

        assertEquals(ErrorCodeEnums.PROFILE_PHONE_EXISTS, exception.getEc());
        verify(userMapper, never()).updateById(any(SysUserEntity.class));
    }

    @Test
    void updatePasswordVerifiesOldPasswordAndStoresEncryptedNewPassword() {
        SysUserEntity doctor = doctor(UserStatusEnums.OK);
        doctor.setPassword("old-password-hash");
        when(userMapper.selectUserById(12L, UserTypeEnums.DOCTOR)).thenReturn(doctor);
        when(userMapper.updateById(doctor)).thenReturn(1);
        when(passwordEncoder.matches("old-password", "old-password-hash")).thenReturn(true);
        when(passwordEncoder.matches("new-password", "old-password-hash")).thenReturn(false);
        when(passwordEncoder.encode("new-password")).thenReturn("new-password-hash");

        UserProfilePasswordRequest request = new UserProfilePasswordRequest();
        request.setOldPassword("old-password");
        request.setNewPassword("new-password");

        userProfileService.updatePassword(loginUser(), request);

        assertEquals("new-password-hash", doctor.getPassword());
        verify(userMapper).updateById(doctor);
    }

    @Test
    void updatePasswordRequestContainsOnlyOldAndNewPassword() {
        assertEquals(
                Set.of("oldPassword", "newPassword"),
                Arrays.stream(UserProfilePasswordRequest.class.getDeclaredFields())
                        .map(field -> field.getName())
                        .collect(java.util.stream.Collectors.toSet()));
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

    private UserProfilePhoneRequest phoneRequest(String phone, String password, String smsCode) {
        UserProfilePhoneRequest request = new UserProfilePhoneRequest();
        request.setPhone(phone);
        request.setPassword(password);
        request.setSmsCode(smsCode);
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
        SysUserEntity user = new SysUserEntity();
        user.setUserId(12L);
        user.setUserType(userType);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(12L);
        loginUser.setUser(user);
        return loginUser;
    }

    private SysUserEntity doctor(UserStatusEnums status) {
        SysUserEntity doctor = new SysUserEntity();
        doctor.setUserId(12L);
        doctor.setUserType(UserTypeEnums.DOCTOR);
        doctor.setStatus(status);
        return doctor;
    }

    private SupplierEntity enabledSupplier() {
        SupplierEntity supplier = new SupplierEntity();
        supplier.setId(1L);
        supplier.setStatus(Boolean.TRUE);
        return supplier;
    }
}
