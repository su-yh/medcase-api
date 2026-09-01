package com.medcase.biz.service;

import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.biz.request.DoctorCasePageRequest;
import com.medcase.biz.request.DoctorCaseSubmitRequest;
import com.medcase.biz.response.DoctorCaseVO;
import com.medcase.biz.domain.DoctorCaseEntity;
import com.medcase.biz.enums.DoctorCaseStatusEnums;
import com.medcase.biz.mapper.DoctorCaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * 医生病例服务
 *
 * @author suyh
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DoctorCaseService {
    private final DoctorCaseMapper doctorCaseMapper;

    public void submit(LoginUser doctorUser, DoctorCaseSubmitRequest request) {
        save(doctorUser, request, DoctorCaseStatusEnums.PENDING_REVIEW);

        log.info("case submit, userId={}, userType={}, id={}",
                doctorUser.getUserId(), doctorUser.getUser().getUserType(), request.getId());
    }

    public void saveDraft(LoginUser loginUser, DoctorCaseSubmitRequest request) {
        save(loginUser, request, DoctorCaseStatusEnums.DRAFT);

        log.info("case save draft, userId={}, userType={}, id={}",
                loginUser.getUserId(), loginUser.getUser().getUserType(), request.getId());
    }

    private void save(LoginUser doctorUser, DoctorCaseSubmitRequest request, DoctorCaseStatusEnums status) {
        Long userId = doctorUser.getUserId();
        UserTypeEnums userType = doctorUser.getUser().getUserType();

        DoctorCaseEntity entity = new DoctorCaseEntity();

        if (request.getId() == null) {
            entity.setUserId(userId);
            entity.setUserNickname(doctorUser.getUser().getNickName());
            entity.setUserType(userType);
        } else {
            DoctorCaseEntity historyEntity = doctorCaseMapper.selectById(request.getId());
            if (historyEntity == null) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND);
            }
            if (!historyEntity.getUserId().equals(userId)
                    || historyEntity.getUserType() != userType) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_REJECT);
            }

            if (historyEntity.getStatus() != DoctorCaseStatusEnums.DRAFT) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_STATUS_NOT_MATCH);
            }

            entity.setId(request.getId());
            entity.setUserType(historyEntity.getUserType());
        }

        entity.setCaseName(request.getCaseName());
        entity.setContent(request.getContent());
        entity.setAttachments(request.getAttachments());
        entity.setStatus(status);

        if (status == DoctorCaseStatusEnums.PENDING_REVIEW) {
            entity.setSubmitTime(new Date());
        }

        if (entity.getId() == null) {
            doctorCaseMapper.insert(entity);
        } else {
            doctorCaseMapper.updateById(entity);
        }
    }

    public void delete(LoginUser loginUser, Long id) {
        Long userId = loginUser.getUserId();
        UserTypeEnums userType = loginUser.getUser().getUserType();
        DoctorCaseEntity entity = doctorCaseMapper.selectById(id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND);
        }
        if (!isCurrentUserCase(entity, userId, userType)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_REJECT);
        }

        if (entity.getStatus() != DoctorCaseStatusEnums.DRAFT) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_DELETE_STATUS_NOT_MATCH);
        }

        if (doctorCaseMapper.deleteById(id) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_DELETE_FAILED);
        }

        log.info("case deleted, userId={}, userType={}, id={}", userId, userType, id);
    }

    public PageResult<DoctorCaseVO> page(
            LoginUser loginUser, PageParam pageParam, DoctorCasePageRequest request) {
        Long userId = loginUser.getUserId();
        UserTypeEnums userType = loginUser.getUser().getUserType();
        PageResult<DoctorCaseEntity> pageResult = doctorCaseMapper.selectCasePage(
                pageParam, userId, userType, request);
        PageResult<DoctorCaseVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(DoctorCaseVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public DoctorCaseVO detail(LoginUser doctorUser, Long id) {
        UserTypeEnums userType = doctorUser.getUser().getUserType();
        DoctorCaseEntity entity = doctorCaseMapper.selectById(id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND);
        }
        if (!isCurrentUserCase(entity, doctorUser.getUserId(), userType)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_REJECT);
        }
        return DoctorCaseVO.fromEntity(entity);
    }

    private boolean isCurrentUserCase(DoctorCaseEntity entity, Long userId, UserTypeEnums userType) {
        return entity.getUserId() != null
                && entity.getUserId().equals(userId)
                && entity.getUserType() == userType;
    }
}
