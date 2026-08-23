package com.ruoyi.biz.caseinfo.mapper;

import com.ruoyi.mp.mybatis.BaseMapperX;
import com.ruoyi.mp.mybatis.LambdaQueryWrapperX;
import com.ruoyi.mp.mybatis.PageParam;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.biz.caseinfo.request.DoctorCaseReviewQuery;
import com.ruoyi.biz.caseinfo.domain.DoctorCaseEntity;
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
        queryWrapper.eqIfPresent(DoctorCaseEntity::getId, query == null ? null : query.getId());
        queryWrapper.likeIfPresent(DoctorCaseEntity::getTitle, query == null ? null : query.getTitle());
        queryWrapper.eqIfPresent(DoctorCaseEntity::getStatus, query == null ? null : query.getStatus());
        queryWrapper.orderByDesc(DoctorCaseEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }

    default DoctorCaseEntity selectAdminCaseById(Long id) {
        if (id == null) {
            return null;
        }

        LambdaQueryWrapperX<DoctorCaseEntity> queryWrapper = build();
        queryWrapper.eq(DoctorCaseEntity::getId, id);
        return selectOne(queryWrapper);
    }
}
