package com.ruoyi.web.service;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import com.ruoyi.web.controller.doctor.request.DoctorCasePageRequest;
import com.ruoyi.web.controller.doctor.request.DoctorCaseSubmitRequest;
import com.ruoyi.web.controller.doctor.response.DoctorCaseVO;
import com.ruoyi.web.domain.DoctorCaseEntity;
import com.ruoyi.web.enums.DoctorCaseStatusEnums;
import com.ruoyi.web.mapper.DoctorCaseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    public void submit(LoginUser loginUser, DoctorCaseSubmitRequest request) {
        Long doctorId = loginUser.getUserId();
        DoctorCaseEntity historyEntity = doctorCaseMapper.selectById(request.getId());
        DoctorCaseEntity entity = new DoctorCaseEntity();

        if (historyEntity != null) {
            if (!historyEntity.getDoctorId().equals(doctorId)) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_REJECT);
            }

            if (historyEntity.getStatus() != DoctorCaseStatusEnums.DRAFT) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_STATUS_NOT_MATCH);
            }

            entity.setId(historyEntity.getId());
        }

        entity.setTitle(request.getTitle());
        entity.setRemark(request.getRemark());
        entity.setAttachments(request.getAttachments());
        entity.setStatus(DoctorCaseStatusEnums.PENDING_REVIEW);

        if (entity.getId() == null) {
            doctorCaseMapper.insert(entity);
        } else {
            doctorCaseMapper.updateById(entity);
        }

        log.info("doctor case submit, doctorId={}, id={}", doctorId, request.getId());
    }

    public void saveDraft(LoginUser loginUser, DoctorCaseSubmitRequest request) {
        Long doctorId = loginUser.getUserId();

        DoctorCaseEntity entity = new DoctorCaseEntity();

        DoctorCaseEntity historyEntity = request.getId() != null
                ? doctorCaseMapper.selectById(request.getId()) : null;
        if (historyEntity != null) {
            if (!historyEntity.getDoctorId().equals(doctorId)) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_REJECT);
            }

            if (historyEntity.getStatus() != DoctorCaseStatusEnums.DRAFT) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_STATUS_NOT_MATCH);
            }

            entity.setId(request.getId());
        } else {
            entity.setDoctorId(doctorId);
            entity.setDoctorNickname(loginUser.getUser().getNickName());
        }

        entity.setTitle(request.getTitle());
        entity.setRemark(request.getRemark());
        entity.setAttachments(request.getAttachments());
        entity.setStatus(DoctorCaseStatusEnums.DRAFT);

        if (entity.getId() == null) {
            doctorCaseMapper.insert(entity);
        } else {
            doctorCaseMapper.updateById(entity);
        }

        log.info("doctor case save draft, doctorId={}, id={}", doctorId, request.getId());
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

    public DoctorCaseVO detail(LoginUser loginUser, Long id) {
        DoctorCaseEntity entity = doctorCaseMapper.selectDoctorCaseById(loginUser.getUserId(), id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND);
        }
        return DoctorCaseVO.fromEntity(entity);
    }

}
