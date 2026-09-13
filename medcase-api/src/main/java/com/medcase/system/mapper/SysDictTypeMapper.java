package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.system.entity.SysDictTypeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysDictTypeMapper extends BaseMapperX<SysDictTypeEntity> {
    default PageResult<SysDictTypeEntity> selectPage(
            PageParam pageParam, String dictName, String status, String dictType,
            Object beginTime, Object endTime) {
        LambdaQueryWrapperX<SysDictTypeEntity> query = build();
        query.likeIfPresent(SysDictTypeEntity::getDictName, dictName);
        query.eqIfPresent(SysDictTypeEntity::getStatus, status);
        query.likeIfPresent(SysDictTypeEntity::getDictType, dictType);
        query.apply(beginTime != null,
                "DATE_FORMAT(create_time, '%Y%m%d') >= DATE_FORMAT({0}, '%Y%m%d')",
                beginTime);
        query.apply(endTime != null,
                "DATE_FORMAT(create_time, '%Y%m%d') <= DATE_FORMAT({0}, '%Y%m%d')",
                endTime);
        return selectPage(pageParam, query);
    }

    default List<SysDictTypeEntity> selectAllDictTypes() {
        LambdaQueryWrapperX<SysDictTypeEntity> query = build();
        return selectList(query);
    }

    default SysDictTypeEntity selectDictTypeByType(String dictType) {
        return selectOne(SysDictTypeEntity::getDictType, dictType);
    }

}
