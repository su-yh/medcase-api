package com.ruoyi.biz.service;

import com.ruoyi.biz.domain.DoctorUserEntity;
import com.ruoyi.biz.mapper.DoctorUserMapper;
import com.ruoyi.biz.request.DoctorUserQuery;
import com.ruoyi.biz.request.DoctorUserReviewRequest;
import com.ruoyi.biz.response.DoctorUserVO;
import com.ruoyi.common.enums.UserStatusEnums;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        user.setStatus(Boolean.TRUE.equals(request.getApprove())
                ? UserStatusEnums.OK
                : UserStatusEnums.REVIEW_FAILED);
        if (doctorUserMapper.updateById(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_USER_REVIEW_FAILED);
        }
    }
}
