package com.medcase.biz.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.biz.request.CaseReviewQuery;
import com.medcase.biz.domain.CaseEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理端病例查询 Mapper
 *
 * @author suyh
 */
@Mapper
public interface CaseAdminMapper extends BaseMapperX<CaseEntity> {
    default PageResult<CaseEntity> selectAdminCasePage(
            PageParam pageParam, CaseReviewQuery query) {
        LambdaQueryWrapperX<CaseEntity> queryWrapper = build();
        queryWrapper.eqIfPresent(CaseEntity::getId, query.getId());
        queryWrapper.likeIfPresent(CaseEntity::getCaseName, query.getCaseName());
        queryWrapper.eqIfPresent(CaseEntity::getStatus, query.getStatus());
        queryWrapper.eqIfPresent(CaseEntity::getUserType, query.getUserType());
        queryWrapper.orderByDesc(CaseEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }

}
