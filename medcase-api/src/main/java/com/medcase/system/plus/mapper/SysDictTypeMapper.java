package com.medcase.system.plus.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.system.plus.entity.SysDictTypeEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysDictTypeMapper extends BaseMapperX<SysDictTypeEntity> {
    default List<SysDictTypeEntity> selectDictTypeList(
            String dictName, String status, String dictType,
            Object beginTime, Object endTime) {
        LambdaQueryWrapper<SysDictTypeEntity> query = build()
                .likeIfPresent(SysDictTypeEntity::getDictName, dictName)
                .eqIfPresent(SysDictTypeEntity::getStatus, status)
                .likeIfPresent(SysDictTypeEntity::getDictType, dictType);
        query.apply(beginTime != null,
                        "DATE_FORMAT(create_time, '%Y%m%d') >= DATE_FORMAT({0}, '%Y%m%d')",
                        beginTime)
                .apply(endTime != null,
                        "DATE_FORMAT(create_time, '%Y%m%d') <= DATE_FORMAT({0}, '%Y%m%d')",
                        endTime);
        return selectList(query);
    }

    default List<SysDictTypeEntity> selectAllDictTypes() {
        return selectList(build());
    }

    default SysDictTypeEntity selectDictTypeById(Long dictId) {
        return selectById(dictId);
    }

    default SysDictTypeEntity selectDictTypeByType(String dictType) {
        return selectOne(SysDictTypeEntity::getDictType, dictType);
    }

    default int insertDictType(SysDictTypeEntity entity) {
        return insert(entity);
    }

    default int updateDictType(SysDictTypeEntity entity) {
        return updateById(entity);
    }

    default int deleteDictTypeById(Long dictId) {
        return deleteById(dictId);
    }
}
