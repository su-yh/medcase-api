package com.medcase.biz.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.biz.request.DoctorCasePageRequest;
import com.medcase.biz.domain.DoctorCaseEntity;
import com.medcase.common.enums.UserTypeEnums;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生病例 Mapper
 *
 * @author suyh
 */
@Mapper
public interface DoctorCaseMapper extends BaseMapperX<DoctorCaseEntity> {
    default PageResult<DoctorCaseEntity> selectCasePage(
            PageParam pageParam, Long userId, UserTypeEnums userType, DoctorCasePageRequest request) {
        LambdaQueryWrapperX<DoctorCaseEntity> queryWrapper = build();
        queryWrapper.eq(DoctorCaseEntity::getUserId, userId);
        queryWrapper.eq(DoctorCaseEntity::getUserType, userType);
        queryWrapper.likeIfPresent(DoctorCaseEntity::getCaseName, request.getCaseNameLike());
        queryWrapper.eqIfPresent(DoctorCaseEntity::getStatus, request.getStatus());
        queryWrapper.geIfPresent(DoctorCaseEntity::getCreateTime, request.getCreateTimeLowerBound());
        queryWrapper.ltIfPresent(DoctorCaseEntity::getCreateTime, request.getCreateTimeUpperBound());
        queryWrapper.orderByDesc(DoctorCaseEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }
}
