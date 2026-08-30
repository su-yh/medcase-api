package com.medcase.biz.service;

import com.medcase.biz.domain.DoctorUserEntity;
import com.medcase.biz.mapper.DoctorUserMapper;
import com.medcase.biz.request.DoctorUserQuery;
import com.medcase.biz.request.DoctorUserReviewRequest;
import com.medcase.biz.response.DoctorUserVO;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.stream.Collectors;

/**
 * 医生管理业务
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorUserService {
    private final DoctorUserMapper doctorUserMapper;

    public PageResult<DoctorUserVO> page(
            PageParam pageParam, @NonNull DoctorUserQuery query) {
        PageResult<DoctorUserEntity> pageResult = doctorUserMapper.selectDoctorPage(
                pageParam, query);
        PageResult<DoctorUserVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(DoctorUserVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public DoctorUserVO detail(Long userId) {
        if (userId == null) {
            return null;
        }

        DoctorUserEntity user = doctorUserMapper.selectDoctorById(userId);
        if (user == null || user.getUserType() != UserTypeEnums.DOCTOR) {
            return null;
        }
        return DoctorUserVO.fromEntity(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void review(Long userId, DoctorUserReviewRequest request) {
        DoctorUserEntity user = doctorUserMapper.selectDoctorById(userId);
        if (user == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_NOT_FOUND);
        }
        if (user.getStatus() != UserStatusEnums.REGISTER
                && user.getStatus() != UserStatusEnums.PENDING_REVIEW) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_REVIEW_STATUS_NOT_MATCH);
        }

        boolean approve = Boolean.TRUE.equals(request.getApprove());
        if (!approve && !StringUtils.hasText(request.getReason())) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_REVIEW_REASON_EMPTY);
        }

        user.setStatus(approve ? UserStatusEnums.OK : UserStatusEnums.REVIEW_FAILED);
        user.setReviewReason(approve ? null : request.getReason().trim());
        if (doctorUserMapper.updateById(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_REVIEW_FAILED);
        }
    }
}
