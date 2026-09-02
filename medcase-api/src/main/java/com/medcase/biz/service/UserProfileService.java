package com.medcase.biz.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.biz.request.UserProfileSubmitRequest;
import com.medcase.biz.response.UserProfileVO;
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
 * 病例端用户资料提交服务
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserMapper userMapper;

    private final Validator validator;

    public UserProfileVO me(LoginUser user) {
        return UserProfileVO.fromEntity(requireUser(user));
    }

    @Transactional(rollbackFor = Exception.class)
    public void submit(LoginUser user, UserProfileSubmitRequest request) {
        UserEntity userEntity = requireUser(user);
        validateSubmitRequest(request, userEntity.getUserType());
        if (userEntity.getStatus() != UserStatusEnums.REGISTER
                && userEntity.getStatus() != UserStatusEnums.REVIEW_FAILED) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_PROFILE_SUBMIT_STATUS_NOT_MATCH);
        }

        userEntity.setNickName(request.getNickName().trim());
        userEntity.setSex(request.getSex().trim());
        userEntity.setPhonenumber(request.getPhone().trim());
        userEntity.setIdCardNumber(request.getIdCardNumber().trim());
        userEntity.setIdCardFront(request.getIdCardFront());
        userEntity.setIdCardBack(request.getIdCardBack());
        if (userEntity.getUserType() == null || userEntity.getUserType() == UserTypeEnums.DOCTOR) {
            userEntity.setTitle(request.getTitle().trim());
            userEntity.setQualificationCertificate(request.getQualificationCertificate());
        } else {
            userEntity.setTitle(null);
            userEntity.setQualificationCertificate(null);
        }
        userEntity.setReviewReason(null);
        userEntity.setStatus(UserStatusEnums.PENDING_REVIEW);
        if (userMapper.updateById(userEntity) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_PROFILE_SUBMIT_FAILED);
        }
    }

    private void validateSubmitRequest(UserProfileSubmitRequest request, UserTypeEnums userType) {
        Set<ConstraintViolation<UserProfileSubmitRequest>> violations = validator.validate(request);
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

    private UserEntity requireUser(LoginUser user) {
        if (user == null || user.getUserId() == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_NOT_FOUND);
        }

        UserTypeEnums userType = user.getUser() == null ? null : user.getUser().getUserType();
        UserEntity userEntity = userMapper.selectUserById(user.getUserId(), userType);
        if (userEntity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_NOT_FOUND);
        }
        return userEntity;
    }
}
