package com.medcase.biz.mapper;

import com.medcase.biz.domain.SupplierEntity;
import com.medcase.biz.request.SupplierQuery;
import com.medcase.common.constant.UserConstants;
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
        LambdaQueryWrapperX<SupplierEntity> queryWrapper = build()
                .likeIfPresent(SupplierEntity::getNickName, query.getNickName())
                .likeIfPresent(SupplierEntity::getPhonenumber, query.getPhone())
                .eqIfPresent(SupplierEntity::getStatus, query.getStatus())
                .orderByDesc(SupplierEntity::getCreateTime);
        return selectPage(pageParam, queryWrapper);
    }

    default boolean existsByNickName(String nickName, Long id) {
        if (!StringUtils.hasText(nickName)) {
            return false;
        }

        LambdaQueryWrapperX<SupplierEntity> queryWrapper = build()
                .eq(SupplierEntity::getNickName, nickName)
                .neIfPresent(SupplierEntity::getId, id);
        return exists(queryWrapper);
    }

    default SupplierEntity selectEnabledById(Long id) {
        if (id == null) {
            return null;
        }
        return selectOne(build()
                .eq(SupplierEntity::getId, id)
                .eq(SupplierEntity::getStatus, UserConstants.NORMAL));
    }

    default List<SupplierEntity> selectEnabledList() {
        return selectList(build()
                .eq(SupplierEntity::getStatus, UserConstants.NORMAL)
                .orderByAsc(SupplierEntity::getNickName)
                .orderByAsc(SupplierEntity::getId));
    }
}
