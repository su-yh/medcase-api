package com.medcase.biz.service;

import com.medcase.common.core.domain.model.LoginUser;
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

        log.info("doctor case submit, doctorId={}, id={}", doctorUser.getUserId(), request.getId());
    }

    public void saveDraft(LoginUser loginUser, DoctorCaseSubmitRequest request) {
        save(loginUser, request, DoctorCaseStatusEnums.DRAFT);

        log.info("doctor case save draft, doctorId={}, id={}", loginUser.getUserId(), request.getId());
    }

    private void save(LoginUser doctorUser, DoctorCaseSubmitRequest request, DoctorCaseStatusEnums status) {
        Long doctorId = doctorUser.getUserId();

        DoctorCaseEntity entity = new DoctorCaseEntity();

        if (request.getId() == null) {
            entity.setDoctorId(doctorId);
            entity.setDoctorNickname(doctorUser.getUser().getNickName());
        } else {
            DoctorCaseEntity historyEntity = doctorCaseMapper.selectById(request.getId());
            if (historyEntity == null) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND);
            }
            if (!historyEntity.getDoctorId().equals(doctorId)) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_REJECT);
            }

            if (historyEntity.getStatus() != DoctorCaseStatusEnums.DRAFT) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_STATUS_NOT_MATCH);
            }

            entity.setId(request.getId());
        }

        entity.setTitle(request.getTitle());
        entity.setRemark(request.getRemark());
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
        Long doctorId = loginUser.getUserId();
        DoctorCaseEntity entity = doctorCaseMapper.selectById(id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND);
        }

        if (!entity.getDoctorId().equals(doctorId)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_REJECT);
        }

        if (entity.getStatus() != DoctorCaseStatusEnums.DRAFT) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_DELETE_STATUS_NOT_MATCH);
        }

        if (doctorCaseMapper.deleteById(id) <= 0) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_DELETE_FAILED);
        }

        log.info("doctor case deleted, doctorId={}, id={}", doctorId, id);
    }

    public PageResult<DoctorCaseVO> page(
            LoginUser loginUser, PageParam pageParam, DoctorCasePageRequest request) {
        Long doctorId = loginUser.getUserId();
        PageResult<DoctorCaseEntity> pageResult = doctorCaseMapper.selectDoctorCasePage(
                pageParam, doctorId, request);
        PageResult<DoctorCaseVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(DoctorCaseVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public DoctorCaseVO detail(LoginUser doctorUser, Long id) {
        DoctorCaseEntity entity = doctorCaseMapper.selectDoctorCaseById(doctorUser.getUserId(), id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND);
        }
        return DoctorCaseVO.fromEntity(entity);
    }

}
