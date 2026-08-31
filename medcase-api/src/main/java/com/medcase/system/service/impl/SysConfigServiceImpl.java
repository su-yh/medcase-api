package com.medcase.system.service.impl;

import java.util.Collection;
import java.util.List;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.common.constant.CacheConstants;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.redis.RedisCache;
import com.medcase.common.core.text.Convert;
import com.medcase.common.utils.StringUtils;
import com.medcase.system.domain.SysConfig;
import com.medcase.system.plus.SystemEntityConverter;
import com.medcase.system.plus.entity.SysConfigEntity;
import com.medcase.system.plus.mapper.SysConfigMapper;
import com.medcase.system.service.ISysConfigService;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import java.util.Date;

/**
 * 参数配置 服务层实现
 * 
 */
@Service
public class SysConfigServiceImpl implements ISysConfigService {

    @Autowired
    private SysConfigMapper configMapper;

    @Autowired
    private RedisCache redisCache;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init() {

        loadingConfigCache();
    }

    /**
     * 查询参数配置信息
     * 
     * @param configId 参数配置ID
     * @return 参数配置信息
     */
    @Override
    public SysConfig selectConfigById(Long configId) {

        return SystemEntityConverter.toDomain(configMapper.selectConfigById(configId));
    }

    /**
     * 根据键名查询参数配置信息
     * 
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey) {

        String configValue = Convert.toStr(redisCache.getCacheObject(getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue)) {

            return configValue;
        }
        SysConfigEntity retConfigEntity = configMapper.selectConfigByKey(configKey);
        SysConfig retConfig = SystemEntityConverter.toDomain(retConfigEntity);
        if (StringUtils.isNotNull(retConfig)) {

            redisCache.setCacheObject(getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取验证码开关
     * 
     * @return true开启，false关闭
     */
    @Override
    public boolean selectCaptchaEnabled() {

        String captchaEnabled = selectConfigByKey("sys.account.captchaEnabled");
        if (StringUtils.isEmpty(captchaEnabled)) {

            return true;
        }
        return Convert.toBool(captchaEnabled);
    }

    /**
     * 查询参数配置列表
     * 
     * @param config 参数配置信息
     * @return 参数配置集合
     */
    @Override
    public List<SysConfig> selectConfigList(SysConfig config) {

        Date beginTime = getDateParam(config, "beginTime");
        Date endTime = getDateParam(config, "endTime");
        return SystemEntityConverter.copyList(configMapper.selectConfigList(
                config.getConfigName(), config.getConfigType(), config.getConfigKey(),
                beginTime, endTime), SysConfig.class);
    }

    /**
     * 新增参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int insertConfig(SysConfig config) {

        SysConfigEntity entity = SystemEntityConverter.toEntity(config);
        int row = configMapper.insertConfig(entity);
        config.setConfigId(entity.getConfigId());
        if (row > 0) {

            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 修改参数配置
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public int updateConfig(SysConfig config) {

        SysConfig temp = selectConfigById(config.getConfigId());
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey())) {

            redisCache.deleteObject(getCacheKey(temp.getConfigKey()));
        }

        int row = configMapper.updateConfig(SystemEntityConverter.toEntity(config));
        if (row > 0) {

            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 批量删除参数信息
     * 
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds) {

        for (Long configId : configIds) {

            SysConfig config = selectConfigById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType())) {

                throw ExceptionUtil.business(ErrorCodeEnums.CONFIG_BUILTIN_DELETE, config.getConfigKey());
            }
            configMapper.deleteConfigById(configId);
            redisCache.deleteObject(getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {

        List<SysConfig> configsList = SystemEntityConverter.copyList(
                configMapper.selectAllConfigs(),
                SysConfig.class);
        for (SysConfig config : configsList) {

            redisCache.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {

        Collection<String> keys = redisCache.keys(CacheConstants.SYS_CONFIG_KEY + "*");
        redisCache.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {

        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     * 
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config) {

        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();
        SysConfig info = SystemEntityConverter.toDomain(
                configMapper.selectConfigByKeyForUnique(config.getConfigKey()));
        if (StringUtils.isNotNull(info) && info.getConfigId().longValue() != configId.longValue()) {

            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey) {

        return CacheConstants.SYS_CONFIG_KEY + configKey;
    }

    private Date getDateParam(SysConfig config, String key) {

        Object value = config.getParams().get(key);
        return value == null ? null : com.medcase.common.utils.DateUtils.parseDate(value);
    }
}
