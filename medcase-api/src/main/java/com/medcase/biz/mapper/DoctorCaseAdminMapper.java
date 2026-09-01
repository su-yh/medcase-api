package com.medcase.biz.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.biz.request.DoctorCaseReviewQuery;
import com.medcase.biz.domain.DoctorCaseEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理端病例查询 Mapper
 *
 * @author suyh
 */
@Mapper
public interface DoctorCaseAdminMapper extends BaseMapperX<DoctorCaseEntity> {
    default PageResult<DoctorCaseEntity> selectAdminCasePage(
            PageParam pageParam, DoctorCaseReviewQuery query) {
        LambdaQueryWrapperX<DoctorCaseEntity> queryWrapper = build();
        queryWrapper.eqIfPresent(DoctorCaseEntity::getId, query.getId());
        queryWrapper.likeIfPresent(DoctorCaseEntity::getCaseName, query.getCaseName());
        queryWrapper.eqIfPresent(DoctorCaseEntity::getStatus, query.getStatus());
        queryWrapper.eqIfPresent(DoctorCaseEntity::getUserType, query.getUserType());
        queryWrapper.orderByDesc(DoctorCaseEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }

}
