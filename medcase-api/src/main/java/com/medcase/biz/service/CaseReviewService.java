package com.medcase.biz.service;

import com.medcase.biz.domain.CaseEntity;
import com.medcase.biz.enums.CaseStatusEnums;
import com.medcase.biz.mapper.CaseAdminMapper;
import com.medcase.biz.request.CaseReviewQuery;
import com.medcase.biz.request.CaseReviewRequest;
import com.medcase.biz.response.CaseReviewVO;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * 病例审核业务
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
public class CaseReviewService {
    private static final String APPROVED_REVIEW_REASON = "通过";

    private final CaseAdminMapper caseAdminMapper;

    public PageResult<CaseReviewVO> page(
            PageParam pageParam, CaseReviewQuery query, UserTypeEnums userType) {
        query.setUserType(userType);
        PageResult<CaseEntity> pageResult =
                caseAdminMapper.selectAdminCasePage(pageParam, query);
        PageResult<CaseReviewVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(CaseReviewVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public CaseReviewVO detail(Long id, UserTypeEnums userType) {
        CaseEntity entity = caseAdminMapper.selectById(id);
        if (entity != null && entity.getUserType() != userType) {
            return null;
        }
        return entity == null ? null : CaseReviewVO.fromEntity(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void review(
            Long id, CaseReviewRequest request, LoginUser adminUser, UserTypeEnums userType) {
        CaseStatusEnums status = CaseStatusEnums.APPROVED_PENDING_SETTLEMENT;
        String reviewReason = APPROVED_REVIEW_REASON;
        if (!request.getApprove()) {
            if (!StringUtils.hasText(request.getReason())) {
                throw ExceptionUtil.business(ErrorCodeEnums.CASE_REVIEW_REASON_EMPTY);
            }
            reviewReason = request.getReason().trim();
            status = CaseStatusEnums.REVIEW_FAILED;
        }

        updateReview(id, status, reviewReason, adminUser, userType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void settle(Long id, LoginUser adminUser, UserTypeEnums userType) {
        CaseEntity entity = caseAdminMapper.selectById(id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_NOT_FOUND);
        }
        if (entity.getUserType() != userType) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_NOT_FOUND);
        }
        if (entity.getStatus() != CaseStatusEnums.APPROVED_PENDING_SETTLEMENT) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_SETTLE_STATUS_NOT_MATCH);
        }

        entity.setStatus(CaseStatusEnums.SETTLED);
        entity.setSettledTime(new Date());
        entity.setSettlerId(adminUser.getUserId());
        entity.setSettlerNickname(adminUser.getUser().getNickName());

        int affectedRows = caseAdminMapper.updateById(entity);
        if (affectedRows <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_SETTLE_FAILED);
        }
    }

    private void updateReview(
            Long id, CaseStatusEnums status, String reviewReason,
            LoginUser adminUser, UserTypeEnums userType) {
        CaseEntity entity = caseAdminMapper.selectById(id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_NOT_FOUND);
        }
        if (entity.getUserType() != userType) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_NOT_FOUND);
        }
        if (entity.getStatus() != CaseStatusEnums.PENDING_REVIEW) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_REVIEW_STATUS_NOT_MATCH);
        }

        entity.setStatus(status);
        entity.setReviewReason(reviewReason);
        entity.setReviewTime(new Date());
        entity.setReviewerId(adminUser.getUserId());
        entity.setReviewerNickname(adminUser.getUser().getNickName());

        int affectedRows = caseAdminMapper.updateById(entity);
        if (affectedRows <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_REVIEW_FAILED);
        }
    }
}
