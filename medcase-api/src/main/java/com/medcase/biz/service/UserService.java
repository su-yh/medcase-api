package com.medcase.biz.service;

import com.medcase.biz.domain.UserEntity;
import com.medcase.biz.mapper.UserMapper;
import com.medcase.biz.request.UserQuery;
import com.medcase.biz.request.UserReviewRequest;
import com.medcase.biz.response.UserVO;
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
 * 用户管理业务
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserMapper userMapper;

    public PageResult<UserVO> page(
            PageParam pageParam, @NonNull UserQuery query, UserTypeEnums userType) {
        PageResult<UserEntity> pageResult = userMapper.selectUserPage(
                pageParam, query, userType);
        PageResult<UserVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(UserVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public PageResult<UserVO> page(PageParam pageParam, @NonNull UserQuery query) {
        PageResult<UserEntity> pageResult = userMapper.selectUserPage(pageParam, query);
        PageResult<UserVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(UserVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public UserVO detail(Long userId) {
        return detail(userId, UserTypeEnums.DOCTOR);
    }

    public UserVO detail(Long userId, UserTypeEnums userType) {
        if (userId == null) {
            return null;
        }

        UserEntity user = userMapper.selectUserById(userId, userType);
        if (user == null || user.getUserType() != userType) {
            return null;
        }
        return UserVO.fromEntity(user);
    }

    public UserVO detailAny(Long userId) {
        if (userId == null) {
            return null;
        }

        UserEntity user = userMapper.selectUserById(userId);
        return user == null ? null : UserVO.fromEntity(user);
    }

    @Transactional(rollbackFor = Exception.class)
    public void review(Long userId, UserReviewRequest request) {
        review(userId, request, UserTypeEnums.DOCTOR);
    }

    @Transactional(rollbackFor = Exception.class)
    public void review(Long userId, UserReviewRequest request, UserTypeEnums userType) {
        UserEntity user = userMapper.selectUserById(userId, userType);
        if (user == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_NOT_FOUND);
        }
        if (user.getStatus() != UserStatusEnums.REGISTER
                && user.getStatus() != UserStatusEnums.PENDING_REVIEW) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_REVIEW_STATUS_NOT_MATCH);
        }

        boolean approve = Boolean.TRUE.equals(request.getApprove());
        if (!approve && !StringUtils.hasText(request.getReason())) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_REVIEW_REASON_EMPTY);
        }

        user.setStatus(approve ? UserStatusEnums.OK : UserStatusEnums.REVIEW_FAILED);
        user.setReviewReason(approve ? null : request.getReason().trim());
        if (userMapper.updateById(user) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_REVIEW_FAILED);
        }
    }
}
