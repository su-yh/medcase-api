package com.medcase.biz.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.biz.request.DoctorCasePageRequest;
import com.medcase.biz.domain.DoctorCaseEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生病例 Mapper
 *
 * @author suyh
 */
@Mapper
public interface DoctorCaseMapper extends BaseMapperX<DoctorCaseEntity> {
    default PageResult<DoctorCaseEntity> selectDoctorCasePage(
            PageParam pageParam, Long doctorId, DoctorCasePageRequest request) {
        LambdaQueryWrapperX<DoctorCaseEntity> queryWrapper = build();
        queryWrapper.eq(DoctorCaseEntity::getDoctorId, doctorId);
        queryWrapper.likeIfPresent(DoctorCaseEntity::getCaseName, request.getCaseNameLike());
        queryWrapper.eqIfPresent(DoctorCaseEntity::getStatus, request.getStatus());
        queryWrapper.geIfPresent(DoctorCaseEntity::getCreateTime, request.getCreateTimeLowerBound());
        queryWrapper.ltIfPresent(DoctorCaseEntity::getCreateTime, request.getCreateTimeUpperBound());
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
