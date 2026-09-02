package com.medcase.biz.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.biz.request.CasePageRequest;
import com.medcase.biz.domain.CaseEntity;
import com.medcase.common.enums.UserTypeEnums;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生病例 Mapper
 *
 * @author suyh
 */
@Mapper
public interface CaseMapper extends BaseMapperX<CaseEntity> {
    default PageResult<CaseEntity> selectCasePage(
            PageParam pageParam, Long userId, UserTypeEnums userType, CasePageRequest request) {
        LambdaQueryWrapperX<CaseEntity> queryWrapper = build();
        queryWrapper.eq(CaseEntity::getUserId, userId);
        queryWrapper.eq(CaseEntity::getUserType, userType);
        queryWrapper.likeIfPresent(CaseEntity::getCaseName, request.getCaseNameLike());
        queryWrapper.eqIfPresent(CaseEntity::getStatus, request.getStatus());
        queryWrapper.geIfPresent(CaseEntity::getCreateTime, request.getCreateTimeLowerBound());
        queryWrapper.ltIfPresent(CaseEntity::getCreateTime, request.getCreateTimeUpperBound());
        queryWrapper.orderByDesc(CaseEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }
}
