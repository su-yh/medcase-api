package com.medcase.biz.mapper;

import com.medcase.biz.domain.SupplierEntity;
import com.medcase.biz.enums.SupplierStatusEnums;
import com.medcase.biz.request.SupplierQuery;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 供应商 Mapper。
 */
@Mapper
public interface SupplierMapper extends BaseMapperX<SupplierEntity> {

    default PageResult<SupplierEntity> selectPage(PageParam pageParam, SupplierQuery query) {
        LambdaQueryWrapperX<SupplierEntity> queryWrapper = build();
        queryWrapper.likeIfPresent(SupplierEntity::getName, query.getName());
        queryWrapper.likeIfPresent(SupplierEntity::getPhonenumber, query.getPhone());
        queryWrapper.eqIfPresent(SupplierEntity::getStatus, query.getStatus());
        queryWrapper.orderByDesc(SupplierEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }

    default boolean existsByName(String name, Long id) {
        if (!StringUtils.hasText(name)) {
            return false;
        }

        LambdaQueryWrapperX<SupplierEntity> queryWrapper = build();
        queryWrapper.eq(SupplierEntity::getName, name);
        queryWrapper.neIfPresent(SupplierEntity::getId, id);
        return exists(queryWrapper);
    }

    default SupplierEntity selectEnabledById(Long id) {
        if (id == null) {
            return null;
        }
        LambdaQueryWrapperX<SupplierEntity> queryWrapper = build();
        queryWrapper.eq(SupplierEntity::getId, id);
        queryWrapper.eq(SupplierEntity::getStatus, SupplierStatusEnums.NORMAL);
        return selectOne(queryWrapper);
    }

    default List<SupplierEntity> selectEnabledList() {
        LambdaQueryWrapperX<SupplierEntity> queryWrapper = build();
        queryWrapper.eq(SupplierEntity::getStatus, SupplierStatusEnums.NORMAL);
        queryWrapper.orderByAsc(SupplierEntity::getName);
        queryWrapper.orderByAsc(SupplierEntity::getId);
        return selectList(queryWrapper);
    }
}
