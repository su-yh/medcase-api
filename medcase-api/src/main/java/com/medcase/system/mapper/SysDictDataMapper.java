package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysDictDataEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysDictDataMapper extends BaseMapperX<SysDictDataEntity> {
    default PageResult<SysDictDataEntity> selectPage(
            PageParam pageParam, String dictType, String dictLabel, String status) {
        LambdaQueryWrapperX<SysDictDataEntity> query = build();
        query.eqIfPresent(SysDictDataEntity::getDictType, dictType);
        query.likeIfPresent(SysDictDataEntity::getDictLabel, dictLabel);
        query.eqIfPresent(SysDictDataEntity::getStatus, status);
        query.orderByAsc(SysDictDataEntity::getDictSort);
        return selectPage(pageParam, query);
    }

    default List<SysDictDataEntity> selectEnabledDictDataByType(String dictType) {
        LambdaQueryWrapperX<SysDictDataEntity> query = build();
        query.eq(SysDictDataEntity::getStatus, "0");
        query.eq(SysDictDataEntity::getDictType, dictType);
        query.orderByAsc(SysDictDataEntity::getDictSort);
        return selectList(query);
    }

    default SysDictDataEntity selectDictLabel(String dictType, String dictValue) {
        return selectOne(SysDictDataEntity::getDictType, dictType,
                SysDictDataEntity::getDictValue, dictValue);
    }

    default Long countByDictType(String dictType) {
        return selectCount(SysDictDataEntity::getDictType, dictType);
    }

    default int updateDictType(String oldDictType, String newDictType) {
        SysDictDataEntity entity = new SysDictDataEntity();
        entity.setDictType(newDictType);
        return update(entity, new LambdaUpdateWrapper<SysDictDataEntity>()
                .eq(SysDictDataEntity::getDictType, oldDictType));
    }

}
