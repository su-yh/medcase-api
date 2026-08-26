package com.ruoyi.biz.service;

import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.biz.mapper.DoctorUserMapper;
import com.ruoyi.biz.request.DoctorProfileSubmitRequest;
import com.ruoyi.biz.response.DoctorProfileVO;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatusEnums;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 医生端资料提交服务
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
public class DoctorProfileService {
    private final DoctorUserMapper doctorUserMapper;

    public DoctorProfileVO me(LoginUser doctorUser) {
        return DoctorProfileVO.fromEntity(requireDoctor(doctorUser));
    }

    @Transactional(rollbackFor = Exception.class)
    public void submit(LoginUser doctorUser, DoctorProfileSubmitRequest request) {
        DoctorUserEntity doctor = requireDoctor(doctorUser);
        if (doctor.getStatus() != UserStatusEnums.REGISTER
                && doctor.getStatus() != UserStatusEnums.REVIEW_FAILED) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_PROFILE_SUBMIT_STATUS_NOT_MATCH);
        }

        doctor.setNickName(request.getName().trim());
        doctor.setPhonenumber(request.getPhone().trim());
        doctor.setStatus(UserStatusEnums.PENDING_REVIEW);
        if (doctorUserMapper.updateById(doctor) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_PROFILE_SUBMIT_FAILED);
        }
    }

    private DoctorUserEntity requireDoctor(LoginUser doctorUser) {
        if (doctorUser == null || doctorUser.getUserId() == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_NOT_FOUND);
        }

        DoctorUserEntity doctor = doctorUserMapper.selectDoctorById(doctorUser.getUserId());
        if (doctor == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_NOT_FOUND);
        }
        return doctor;
    }
}
