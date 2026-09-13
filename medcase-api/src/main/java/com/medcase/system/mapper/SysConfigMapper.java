package com.medcase.system.mapper;

import com.medcase.mp.mybatis.BaseMapperX;
import com.medcase.mp.mybatis.LambdaQueryWrapperX;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysConfigEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface SysConfigMapper extends BaseMapperX<SysConfigEntity> {
    default SysConfigEntity selectConfigByKey(String configKey) {
        return selectOne(SysConfigEntity::getConfigKey, configKey);
    }

    default PageResult<SysConfigEntity> selectPage(
            PageParam pageParam, String configName, String configType, String configKey,
            Date beginTime, Date endTime) {
        LambdaQueryWrapperX<SysConfigEntity> query = build();
        query.likeIfPresent(SysConfigEntity::getConfigName, configName);
        query.eqIfPresent(SysConfigEntity::getConfigType, configType);
        query.likeIfPresent(SysConfigEntity::getConfigKey, configKey);
        query.geIfPresent(SysConfigEntity::getCreateTime, beginTime);
        query.leIfPresent(SysConfigEntity::getCreateTime, endTime);
        return selectPage(pageParam, query);
    }

    default List<SysConfigEntity> selectAllConfigs() {
        LambdaQueryWrapperX<SysConfigEntity> query = build();
        return selectList(query);
    }

}
