package com.medcase.system.plus.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.system.plus.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface SysConfigMapper extends BaseMapperX<SysConfigEntity> {
    default SysConfigEntity selectConfigById(Long configId) {
        return selectById(configId);
    }

    default SysConfigEntity selectConfigByKey(String configKey) {
        return selectOne(SysConfigEntity::getConfigKey, configKey);
    }

    default List<SysConfigEntity> selectConfigList(
            String configName, String configType, String configKey,
            Date beginTime, Date endTime) {
        LambdaQueryWrapper<SysConfigEntity> query = build()
                .likeIfPresent(SysConfigEntity::getConfigName, configName)
                .eqIfPresent(SysConfigEntity::getConfigType, configType)
                .likeIfPresent(SysConfigEntity::getConfigKey, configKey)
                .geIfPresent(SysConfigEntity::getCreateTime, beginTime)
                .leIfPresent(SysConfigEntity::getCreateTime, endTime);
        return selectList(query);
    }

    default List<SysConfigEntity> selectAllConfigs() {
        return selectList(build());
    }

    default int insertConfig(SysConfigEntity entity) {
        return insert(entity);
    }

    default int updateConfig(SysConfigEntity entity) {
        return updateById(entity);
    }

    default int deleteConfigById(Long configId) {
        return deleteById(configId);
    }

    default SysConfigEntity selectConfigByKeyForUnique(String configKey) {
        return selectConfigByKey(configKey);
    }
}
