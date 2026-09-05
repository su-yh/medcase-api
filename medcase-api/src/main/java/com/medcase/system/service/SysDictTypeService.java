package com.medcase.system.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.common.core.domain.entity.SysDictType;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.entity.SysDictTypeEntity;
import com.medcase.system.mapper.SysDictDataMapper;
import com.medcase.system.mapper.SysDictTypeMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 字典 业务层处理
 * 
 */
@Service
public class SysDictTypeService {

    private static final long DICT_CACHE_EXPIRE_MINUTES = 30L;

    private final Cache<String, List<SysDictDataEntity>> dictCache = Caffeine.newBuilder()
            .expireAfterWrite(DICT_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .build();

    private final Object dictCacheLoadLock = new Object();

    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 项目启动时，初始化字典到缓存
     */
    @PostConstruct
    public void init() {

        loadingDictCache();
    }

    public PageResult<SysDictType> selectPage(PageParam pageParam, SysDictType dictType) {

        Object beginTime = dictType.getParams().get("beginTime");
        Object endTime = dictType.getParams().get("endTime");
        PageResult<SysDictTypeEntity> entityPage = dictTypeMapper.selectPage(
                pageParam, dictType.getDictName(), dictType.getStatus(), dictType.getDictType(),
                beginTime, endTime);
        PageResult<SysDictType> result = new PageResult<>();
        result.setList(SystemEntityConverter.copyList(entityPage.getList(), SysDictType.class));
        result.setTotal(entityPage.getTotal());
        return result;
    }

    /**
     * 根据所有字典类型
     * 
     * @return 字典类型集合信息
     */
    public List<SysDictType> selectDictTypeAll() {

        return SystemEntityConverter.copyList(dictTypeMapper.selectAllDictTypes(), SysDictType.class);
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

        List<SysDictDataEntity> dictDatas = dictCache.getIfPresent(dictType);
        if (dictDatas == null) {
            synchronized (dictCacheLoadLock) {
                dictDatas = dictCache.getIfPresent(dictType);
                if (dictDatas == null) {
                    dictDatas = dictDataMapper.selectEnabledDictDataByType(dictType);
                    if (dictDatas == null) {
                        dictDatas = List.of();
                    }
                    dictCache.put(dictType, List.copyOf(dictDatas));
                }
            }
        }
        if (CollectionUtils.isEmpty(dictDatas)) {
            return null;
        }
        return SystemEntityConverter.copyList(new ArrayList<>(dictDatas), SysDictData.class);
    }

    /**
     * 根据字典类型ID查询信息
     * 
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    public SysDictType selectDictTypeById(Long dictId) {

        return SystemEntityConverter.toDomain(dictTypeMapper.selectById(dictId));
    }


    /**
     * 批量删除字典类型信息
     * 
     * @param dictIds 需要删除的字典ID
     */
    public void deleteDictTypeByIds(Long[] dictIds) {

        for (Long dictId : dictIds) {

            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countByDictType(dictType.getDictType()) > 0) {

                throw ExceptionUtil.business(ErrorCodeEnums.DICT_TYPE_ASSIGNED_DELETE, dictType.getDictName());
            }
            dictTypeMapper.deleteById(dictId);
            clearDictCache(dictType.getDictType());
        }
    }

    /**
     * 加载字典缓存数据
     */
    public void loadingDictCache() {

        List<SysDictDataEntity> dictDatas = dictDataMapper.selectDictDataList(null, null, "0");
        if (dictDatas == null) {
            dictDatas = List.of();
        }
        Map<String, List<SysDictDataEntity>> dictDataMap = dictDatas.stream()
                .filter(dictData -> StringUtils.hasText(dictData.getDictType()))
                .collect(Collectors.groupingBy(SysDictDataEntity::getDictType));
        synchronized (dictCacheLoadLock) {
            dictCache.invalidateAll();
            for (Map.Entry<String, List<SysDictDataEntity>> entry : dictDataMap.entrySet()) {
                List<SysDictDataEntity> sortedDictDatas = entry.getValue().stream()
                        .sorted(Comparator.comparing(SysDictDataEntity::getDictSort))
                        .collect(Collectors.toList());
                dictCache.put(entry.getKey(), List.copyOf(sortedDictDatas));
            }
        }
    }

    /**
     * 清空字典缓存数据
     */
    public void clearDictCache() {

        dictCache.invalidateAll();
    }

    /**
     * 清空指定字典类型缓存数据
     *
     * @param dictType 字典类型
     */
    public void clearDictCache(String dictType) {

        if (StringUtils.hasText(dictType)) {
            dictCache.invalidate(dictType);
        }
    }

    /**
     * 重置字典缓存数据
     */
    public void resetDictCache() {

        clearDictCache();
        loadingDictCache();
    }

    /**
     * 新增保存字典类型信息
     * 
     * @param dict 字典类型信息
     * @return 结果
     */
    public int insertDictType(SysDictType dict) {

        SysDictTypeEntity entity = SystemEntityConverter.toEntity(dict);
        int row = dictTypeMapper.insert(entity);
        dict.setDictId(entity.getDictId());
        return row;
    }

    /**
     * 修改保存字典类型信息
     * 
     * @param dict 字典类型信息
     * @return 结果
     */
    @Transactional
    public int updateDictType(SysDictType dict) {

        SysDictType oldDict = selectDictTypeById(dict.getDictId());
        dictDataMapper.updateDictType(oldDict.getDictType(), dict.getDictType());
        int row = dictTypeMapper.updateById(SystemEntityConverter.toEntity(dict));
        if (row > 0) {

            clearDictCache(oldDict.getDictType());
            clearDictCache(dict.getDictType());
        }
        return row;
    }

    /**
     * 校验字典类型称是否唯一
     * 
     * @param dict 字典类型
     * @return 结果
     */
    public boolean checkDictTypeUnique(SysDictType dict) {

        Long dictId = dict.getDictId() == null ? -1L : dict.getDictId();
        SysDictType dictType = SystemEntityConverter.toDomain(
                dictTypeMapper.selectDictTypeByType(dict.getDictType()));
        if (dictType != null && dictType.getDictId().longValue() != dictId.longValue()) {

            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}
