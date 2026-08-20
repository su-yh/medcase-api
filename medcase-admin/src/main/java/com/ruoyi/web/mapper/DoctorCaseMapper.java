package com.ruoyi.web.mapper;

import com.ruoyi.mp.mybatis.BaseMapperX;
import com.ruoyi.mp.mybatis.LambdaQueryWrapperX;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.web.domain.DoctorCaseEntity;
import com.ruoyi.web.enums.DoctorCaseStatusEnums;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生病例 Mapper
 *
 * @author suyh
 */
@Mapper
public interface DoctorCaseMapper extends BaseMapperX<DoctorCaseEntity> {
    default PageResult<DoctorCaseEntity> selectDoctorCasePage(
            Long doctorId, DoctorCaseStatusEnums status, PageParam pageParam) {
        LambdaQueryWrapperX<DoctorCaseEntity> queryWrapper = build();
        queryWrapper.eq(DoctorCaseEntity::getDoctorId, doctorId);
        queryWrapper.eqIfPresent(DoctorCaseEntity::getStatus, status);
        queryWrapper.orderByDesc(DoctorCaseEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }

    default DoctorCaseEntity selectDoctorCaseById(Long doctorId, Long id) {
        if (doctorId == null || id == null) {
            return null;
        }

        LambdaQueryWrapperX<DoctorCaseEntity> queryWrapper = build();
        queryWrapper.eq(DoctorCaseEntity::getDoctorId, doctorId);
        queryWrapper.eq(DoctorCaseEntity::getId, id);
        return selectOne(queryWrapper);
    }
}
