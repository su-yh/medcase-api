package com.medcase.system.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.mapper.SysDictDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 字典 业务层处理
 * 
 */
@Service
public class SysDictDataService {

    private static final long DICT_CACHE_EXPIRE_MINUTES = 30L;

    private final Cache<String, List<SysDictDataEntity>> dictDataCache = Caffeine.newBuilder()
            .expireAfterWrite(DICT_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .build();

    private final Object dictDataCacheLoadLock = new Object();

    @Autowired
    private SysDictDataMapper dictDataMapper;

    public PageResult<SysDictData> selectPage(PageParam pageParam, SysDictData dictData) {

        PageResult<SysDictDataEntity> entityPage = dictDataMapper.selectPage(
                pageParam, dictData.getDictType(), dictData.getDictLabel(), dictData.getStatus());
        PageResult<SysDictData> result = new PageResult<>();
        result.setList(SystemEntityConverter.copyList(entityPage.getList(), SysDictData.class));
        result.setTotal(entityPage.getTotal());
        return result;
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    public List<SysDictData> selectDictDataByType(String dictType) {

        if (!StringUtils.hasText(dictType)) {
            return null;
        }

        List<SysDictDataEntity> dictDatas = dictDataCache.getIfPresent(dictType);
        if (dictDatas == null) {
            synchronized (dictDataCacheLoadLock) {
                dictDatas = dictDataCache.getIfPresent(dictType);
                if (dictDatas == null) {
                    dictDatas = dictDataMapper.selectEnabledDictDataByType(dictType);
                    if (dictDatas == null) {
                        dictDatas = List.of();
                    }
                    dictDataCache.put(dictType, List.copyOf(dictDatas));
                }
            }
        }
        if (CollectionUtils.isEmpty(dictDatas)) {
            return null;
        }
        return SystemEntityConverter.copyList(new ArrayList<>(dictDatas), SysDictData.class);
    }

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    public SysDictData selectDictDataById(Long dictCode) {

        return SystemEntityConverter.toDomain(dictDataMapper.selectById(dictCode));
    }

    /**
     * 清空字典数据缓存
     */
    public void clearDictCache() {

        dictDataCache.invalidateAll();
    }

    /**
     * 清空指定字典数据缓存
     *
     * @param dictType 字典类型
     */
    public void clearDictDataCache(String dictType) {

        if (StringUtils.hasText(dictType)) {
            dictDataCache.invalidate(dictType);
        }
    }

    /**
     * 批量删除字典数据信息
     * 
     * @param dictCodes 需要删除的字典数据ID
     */
    public void deleteDictDataByIds(Long[] dictCodes) {

        for (Long dictCode : dictCodes) {

            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteById(dictCode);
            clearDictDataCache(data.getDictType());
        }
    }

    /**
     * 新增保存字典数据信息
     * 
     * @param data 字典数据信息
     * @return 结果
     */
    public int insertDictData(SysDictData data) {

        SysDictDataEntity entity = SystemEntityConverter.toEntity(data);
        int row = dictDataMapper.insert(entity);
        data.setDictCode(entity.getDictCode());
        if (row > 0) {

            clearDictDataCache(data.getDictType());
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     * 
     * @param data 字典数据信息
     * @return 结果
     */
    public int updateDictData(SysDictData data) {

        SysDictData oldData = selectDictDataById(data.getDictCode());
        int row = dictDataMapper.updateById(SystemEntityConverter.toEntity(data));
        if (row > 0) {

            if (oldData != null) {
                clearDictDataCache(oldData.getDictType());
            }
            clearDictDataCache(data.getDictType());
        }
        return row;
    }
}
