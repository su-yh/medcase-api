package com.medcase.biz.service;

import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.biz.request.CasePageRequest;
import com.medcase.biz.request.CaseSubmitRequest;
import com.medcase.biz.response.CaseVO;
import com.medcase.biz.domain.CaseEntity;
import com.medcase.biz.enums.CaseStatusEnums;
import com.medcase.biz.mapper.CaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * 病例服务
 *
 * @author suyh
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CaseService {
    private final CaseMapper caseMapper;

    public void submit(LoginUser user, CaseSubmitRequest request) {
        save(user, request, CaseStatusEnums.PENDING_REVIEW);

        log.info("case submit, userId={}, userType={}, id={}",
                user.getUserId(), user.getUser().getUserType(), request.getId());
    }

    public void saveDraft(LoginUser loginUser, CaseSubmitRequest request) {
        save(loginUser, request, CaseStatusEnums.DRAFT);

        log.info("case save draft, userId={}, userType={}, id={}",
                loginUser.getUserId(), loginUser.getUser().getUserType(), request.getId());
    }

    private void save(LoginUser user, CaseSubmitRequest request, CaseStatusEnums status) {
        Long userId = user.getUserId();
        UserTypeEnums userType = user.getUser().getUserType();

        CaseEntity entity = new CaseEntity();

        if (request.getId() == null) {
            entity.setUserId(userId);
            entity.setUserNickname(user.getUser().getNickName());
            entity.setUserType(userType);
        } else {
            CaseEntity historyEntity = caseMapper.selectById(request.getId());
            if (historyEntity == null) {
                throw ExceptionUtil.business(ErrorCodeEnums.CASE_NOT_FOUND);
            }
            if (!historyEntity.getUserId().equals(userId)
                    || historyEntity.getUserType() != userType) {
                throw ExceptionUtil.business(ErrorCodeEnums.CASE_UPDATE_REJECT);
            }

            if (historyEntity.getStatus() != CaseStatusEnums.DRAFT) {
                throw ExceptionUtil.business(ErrorCodeEnums.CASE_UPDATE_STATUS_NOT_MATCH);
            }

            entity.setId(request.getId());
            entity.setUserType(historyEntity.getUserType());
        }

        entity.setCaseName(request.getCaseName());
        entity.setContent(request.getContent());
        entity.setAttachments(request.getAttachments());
        entity.setStatus(status);

        if (status == CaseStatusEnums.PENDING_REVIEW) {
            entity.setSubmitTime(new Date());
        }

        if (entity.getId() == null) {
            caseMapper.insert(entity);
        } else {
            caseMapper.updateById(entity);
        }
    }

    public void delete(LoginUser loginUser, Long id) {
        Long userId = loginUser.getUserId();
        UserTypeEnums userType = loginUser.getUser().getUserType();
        CaseEntity entity = caseMapper.selectById(id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_NOT_FOUND);
        }
        if (!isCurrentUserCase(entity, userId, userType)) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_UPDATE_REJECT);
        }

        if (entity.getStatus() != CaseStatusEnums.DRAFT) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_DELETE_STATUS_NOT_MATCH);
        }

        if (caseMapper.deleteById(id) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_DELETE_FAILED);
        }

        log.info("case deleted, userId={}, userType={}, id={}", userId, userType, id);
    }

    public PageResult<CaseVO> page(
            LoginUser loginUser, PageParam pageParam, CasePageRequest request) {
        Long userId = loginUser.getUserId();
        UserTypeEnums userType = loginUser.getUser().getUserType();
        PageResult<CaseEntity> pageResult = caseMapper.selectCasePage(
                pageParam, userId, userType, request);
        PageResult<CaseVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(CaseVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public CaseVO detail(LoginUser user, Long id) {
        UserTypeEnums userType = user.getUser().getUserType();
        CaseEntity entity = caseMapper.selectById(id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_NOT_FOUND);
        }
        if (!isCurrentUserCase(entity, user.getUserId(), userType)) {
            throw ExceptionUtil.business(ErrorCodeEnums.CASE_UPDATE_REJECT);
        }
        return CaseVO.fromEntity(entity);
    }

    private boolean isCurrentUserCase(CaseEntity entity, Long userId, UserTypeEnums userType) {
        return entity.getUserId() != null
                && entity.getUserId().equals(userId)
                && entity.getUserType() == userType;
    }
}
