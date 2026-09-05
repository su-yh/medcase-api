package com.medcase.system.mapper;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysDictDataEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysDictDataMapper extends BaseMapperX<SysDictDataEntity> {
    default PageResult<SysDictDataEntity> selectPage(
            PageParam pageParam, String dictType, String dictLabel, String status) {
        LambdaQueryWrapper<SysDictDataEntity> query = build()
                .eqIfPresent(SysDictDataEntity::getDictType, dictType)
                .likeIfPresent(SysDictDataEntity::getDictLabel, dictLabel)
                .eqIfPresent(SysDictDataEntity::getStatus, status)
                .orderByAsc(SysDictDataEntity::getDictSort);
        return selectPage(pageParam, query);
    }

    default List<SysDictDataEntity> selectEnabledDictDataByType(String dictType) {
        return selectList(build()
                .eq(SysDictDataEntity::getStatus, "0")
                .eq(SysDictDataEntity::getDictType, dictType)
                .orderByAsc(SysDictDataEntity::getDictSort));
    }

    default SysDictDataEntity selectDictLabel(String dictType, String dictValue) {
        return selectOne(SysDictDataEntity::getDictType, dictType,
                SysDictDataEntity::getDictValue, dictValue);
    }

    default Long countByDictType(String dictType) {
        return selectCount(SysDictDataEntity::getDictType, dictType);
    }

    default int updateDictType(String oldDictType, String newDictType) {
        return update(null, new LambdaUpdateWrapper<SysDictDataEntity>()
                .set(SysDictDataEntity::getDictType, newDictType)
                .eq(SysDictDataEntity::getDictType, oldDictType));
    }

}
