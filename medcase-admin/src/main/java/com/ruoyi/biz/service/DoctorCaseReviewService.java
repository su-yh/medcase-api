package com.ruoyi.biz.service;

import com.ruoyi.biz.domain.DoctorCaseEntity;
import com.ruoyi.biz.enums.DoctorCaseStatusEnums;
import com.ruoyi.biz.mapper.DoctorCaseAdminMapper;
import com.ruoyi.biz.request.DoctorCaseReviewQuery;
import com.ruoyi.biz.request.DoctorCaseReviewRequest;
import com.ruoyi.biz.response.DoctorCaseReviewVO;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
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
public class DoctorCaseReviewService {
    private static final String APPROVED_REVIEW_REASON = "通过";

    private final DoctorCaseAdminMapper doctorCaseAdminMapper;

    public PageResult<DoctorCaseReviewVO> page(
            Integer pageNum, Integer pageSize, DoctorCaseReviewQuery query) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNum == null || pageNum < 1 ? PageParam.PAGE_NO : pageNum);
        pageParam.setPageSize(pageSize == null || pageSize < 1 ? PageParam.PAGE_SIZE : pageSize);

        PageResult<DoctorCaseEntity> pageResult =
                doctorCaseAdminMapper.selectAdminCasePage(pageParam, query);
        PageResult<DoctorCaseReviewVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(DoctorCaseReviewVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public DoctorCaseReviewVO detail(Long id) {
        DoctorCaseEntity entity = doctorCaseAdminMapper.selectAdminCaseById(id);
        return entity == null ? null : DoctorCaseReviewVO.fromEntity(entity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void review(Long id, DoctorCaseReviewRequest request, LoginUser adminUser) {
        DoctorCaseStatusEnums status = DoctorCaseStatusEnums.APPROVED_PENDING_SETTLEMENT;
        String reviewReason = APPROVED_REVIEW_REASON;
        if (!request.getApprove()) {
            if (!StringUtils.hasText(request.getReason())) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_REVIEW_REASON_EMPTY);
            }
            reviewReason = request.getReason().trim();
            status = DoctorCaseStatusEnums.REVIEW_FAILED;
        }

        updateReview(id, status, reviewReason, adminUser);
    }

    private void updateReview(
            Long id, DoctorCaseStatusEnums status, String reviewReason, LoginUser adminUser) {
        DoctorCaseEntity entity = doctorCaseAdminMapper.selectById(id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND);
        }
        if (entity.getStatus() != DoctorCaseStatusEnums.PENDING_REVIEW) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_REVIEW_STATUS_NOT_MATCH);
        }

        entity.setStatus(status);
        entity.setReviewReason(reviewReason);
        entity.setReviewTime(new Date());
        entity.setReviewerId(adminUser.getUserId());
        entity.setReviewerNickname(adminUser.getUser().getNickName());

        int affectedRows = doctorCaseAdminMapper.updateById(entity);
        if (affectedRows <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_REVIEW_FAILED);
        }
    }
}
