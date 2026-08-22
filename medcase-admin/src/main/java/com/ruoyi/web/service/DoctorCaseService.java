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

    public void update(LoginUser loginUser, DoctorCaseSubmitRequest request, DoctorCaseStatusEnums status) {
        Long doctorId = loginUser.getUserId();
        DoctorCaseEntity historyEntity = doctorCaseMapper.selectById(request.getId());
        if (!historyEntity.getDoctorId().equals(doctorId)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_REJECT);
        }

        if (historyEntity.getStatus() != DoctorCaseStatusEnums.DRAFT) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_UPDATE_STATUS_NOT_MATCH);
        }

        DoctorCaseEntity entity = new DoctorCaseEntity();
        entity.setId(request.getId());
        entity.setTitle(request.getTitle());
        entity.setRemark(request.getRemark());
        entity.setAttachments(request.getAttachments());
        entity.setStatus(status);

        doctorCaseMapper.updateById(entity);
    }

    public void save(LoginUser loginUser, DoctorCaseSubmitRequest request, DoctorCaseStatusEnums status) {
        Long doctorId = loginUser.getUserId();
        DoctorCaseEntity entity = new DoctorCaseEntity();
        entity.setDoctorId(doctorId);
        entity.setDoctorNickname(loginUser.getUser().getNickName());
        entity.setTitle(request.getTitle());
        entity.setRemark(request.getRemark());
        entity.setAttachments(request.getAttachments());
        entity.setStatus(status);

        if (doctorCaseMapper.insert(entity) <= 0) {
            log.error("doctor case save failed, doctorId={}", doctorId);
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_SUBMIT_FAILED);
        }

        log.info("doctor case submitted, doctorId={}, id={}", doctorId, entity.getId());
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
