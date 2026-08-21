package com.ruoyi.web.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import com.ruoyi.web.controller.doctor.request.DoctorCaseSubmitRequest;
import com.ruoyi.web.controller.doctor.response.DoctorCaseVO;
import com.ruoyi.web.domain.DoctorCaseEntity;
import com.ruoyi.web.enums.DoctorCaseStatusEnums;
import com.ruoyi.web.mapper.DoctorCaseMapper;
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

    public void submit(DoctorCaseSubmitRequest request) {
        SysUser doctor = currentDoctor();
        Long doctorId = doctor.getUserId();
        Date now = new Date();
        DoctorCaseEntity entity = new DoctorCaseEntity();
        entity.setDoctorId(doctorId);
        entity.setDoctorNickname(doctor.getNickName());
        entity.setRemark(request.getRemark());
        entity.setAttachments(request.getAttachments());
        entity.setStatus(DoctorCaseStatusEnums.PENDING_REVIEW);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        if (doctorCaseMapper.insert(entity) <= 0) {
            log.error("doctor case submit failed, doctorId={}", doctorId);
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_SUBMIT_FAILED);
        }

        log.info("doctor case submitted, doctorId={}, id={}", doctorId, entity.getId());
    }

    public PageResult<DoctorCaseVO> page(PageParam pageParam, DoctorCaseStatusEnums status) {
        Long doctorId = currentDoctorId();
        PageResult<DoctorCaseEntity> pageResult = doctorCaseMapper.selectDoctorCasePage(doctorId, status, pageParam);
        PageResult<DoctorCaseVO> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setList(pageResult.getList().stream()
                .map(DoctorCaseVO::fromEntity)
                .collect(Collectors.toList()));
        return result;
    }

    public DoctorCaseVO detail(Long id) {
        DoctorCaseEntity entity = doctorCaseMapper.selectDoctorCaseById(currentDoctorId(), id);
        if (entity == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_CASE_NOT_FOUND);
        }
        return DoctorCaseVO.fromEntity(entity);
    }

    private Long currentDoctorId() {
        return currentDoctor().getUserId();
    }

    private SysUser currentDoctor() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        if (user == null || !UserTypeEnums.DOCTOR.equals(user.getUserType())) {
            throw ExceptionUtil.business(ErrorCodeEnums.ACCESS_DENIED);
        }
        return user;
    }
}
