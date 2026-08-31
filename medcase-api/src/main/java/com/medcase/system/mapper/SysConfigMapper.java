package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.medcase.system.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface SysConfigMapper extends BaseMapperX<SysConfigEntity> {
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

}
