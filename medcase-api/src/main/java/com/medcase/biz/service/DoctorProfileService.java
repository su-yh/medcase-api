package com.medcase.biz.service;

import com.medcase.biz.domain.DoctorUserEntity;
import com.medcase.biz.mapper.DoctorUserMapper;
import com.medcase.biz.request.DoctorProfileSubmitRequest;
import com.medcase.biz.response.DoctorProfileVO;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.validation.groups.ValidationGroups;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * 医生端资料提交服务
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
public class DoctorProfileService {
    private final DoctorUserMapper doctorUserMapper;

    private final Validator validator;

    public DoctorProfileVO me(LoginUser doctorUser) {
        return DoctorProfileVO.fromEntity(requireUser(doctorUser));
    }

    @Transactional(rollbackFor = Exception.class)
    public void submit(LoginUser doctorUser, DoctorProfileSubmitRequest request) {
        DoctorUserEntity doctor = requireUser(doctorUser);
        validateSubmitRequest(request, doctor.getUserType());
        if (doctor.getStatus() != UserStatusEnums.REGISTER
                && doctor.getStatus() != UserStatusEnums.REVIEW_FAILED) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_PROFILE_SUBMIT_STATUS_NOT_MATCH);
        }

        doctor.setNickName(request.getNickName().trim());
        doctor.setSex(request.getSex().trim());
        doctor.setPhonenumber(request.getPhone().trim());
        doctor.setIdCardNumber(request.getIdCardNumber().trim());
        doctor.setIdCardFront(request.getIdCardFront());
        doctor.setIdCardBack(request.getIdCardBack());
        if (doctor.getUserType() == null || doctor.getUserType() == UserTypeEnums.DOCTOR) {
            doctor.setTitle(request.getTitle().trim());
            doctor.setQualificationCertificate(request.getQualificationCertificate());
        } else {
            doctor.setTitle(null);
            doctor.setQualificationCertificate(null);
        }
        doctor.setReviewReason(null);
        doctor.setStatus(UserStatusEnums.PENDING_REVIEW);
        if (doctorUserMapper.updateById(doctor) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_PROFILE_SUBMIT_FAILED);
        }
    }

    private void validateSubmitRequest(DoctorProfileSubmitRequest request, UserTypeEnums userType) {
        Set<ConstraintViolation<DoctorProfileSubmitRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        if (userType == null || userType == UserTypeEnums.DOCTOR) {
            violations = validator.validate(request, ValidationGroups.Doctor.Submit.class);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
        }
    }

    private DoctorUserEntity requireUser(LoginUser doctorUser) {
        if (doctorUser == null || doctorUser.getUserId() == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_NOT_FOUND);
        }

        UserTypeEnums userType = doctorUser.getUser() == null ? null : doctorUser.getUser().getUserType();
        DoctorUserEntity doctor = doctorUserMapper.selectUserById(doctorUser.getUserId(), userType);
        if (doctor == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_NOT_FOUND);
        }
        return doctor;
    }
}
