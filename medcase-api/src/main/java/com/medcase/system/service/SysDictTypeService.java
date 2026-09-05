package com.medcase.system.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.entity.SysDictType;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysDictTypeEntity;
import com.medcase.system.mapper.SysDictDataMapper;
import com.medcase.system.mapper.SysDictTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 字典 业务层处理
 * 
 */
@Service
public class SysDictTypeService {

    private static final long DICT_CACHE_EXPIRE_MINUTES = 30L;

    private final Cache<Long, SysDictTypeEntity> dictTypeCache = Caffeine.newBuilder()
            .expireAfterWrite(DICT_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
            .build();

    private final Object dictTypeCacheLoadLock = new Object();

    @Autowired
    private SysDictTypeMapper dictTypeMapper;

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Autowired
    private SysDictDataService dictDataService;

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
     * 根据字典类型ID查询信息
     * 
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    public SysDictType selectDictTypeById(Long dictId) {

        if (dictId == null) {
            return null;
        }
        SysDictTypeEntity dictType = dictTypeCache.getIfPresent(dictId);
        if (dictType == null) {
            synchronized (dictTypeCacheLoadLock) {
                dictType = dictTypeCache.getIfPresent(dictId);
                if (dictType == null) {
                    dictType = dictTypeMapper.selectById(dictId);
                    if (dictType != null) {
                        dictTypeCache.put(dictId, dictType);
                    }
                }
            }
        }
        return SystemEntityConverter.toDomain(dictType);
    }


    /**
     * 批量删除字典类型信息
     * 
     * @param dictIds 需要删除的字典ID
     */
    public void deleteDictTypeByIds(Long[] dictIds) {

        for (Long dictId : dictIds) {

            SysDictType dictType = SystemEntityConverter.toDomain(dictTypeMapper.selectById(dictId));
            if (dictDataMapper.countByDictType(dictType.getDictType()) > 0) {

                throw ExceptionUtil.business(ErrorCodeEnums.DICT_TYPE_ASSIGNED_DELETE, dictType.getDictName());
            }
            dictTypeMapper.deleteById(dictId);
            clearDictTypeCache(dictId);
            dictDataService.clearDictDataCache(dictType.getDictType());
        }
    }

    /**
     * 清空字典缓存数据
     */
    public void clearDictCache() {

        dictTypeCache.invalidateAll();
        dictDataService.clearDictCache();
    }

    /**
     * 清空指定字典类型缓存数据
     *
     * @param dictId 字典类型ID
     */
    public void clearDictTypeCache(Long dictId) {

        if (dictId != null) {
            dictTypeCache.invalidate(dictId);
        }
    }

    /**
     * 重置字典缓存数据
     */
    public void resetDictCache() {

        clearDictCache();
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

        SysDictType oldDict = SystemEntityConverter.toDomain(
                dictTypeMapper.selectById(dict.getDictId()));
        dictDataMapper.updateDictType(oldDict.getDictType(), dict.getDictType());
        int row = dictTypeMapper.updateById(SystemEntityConverter.toEntity(dict));
        if (row > 0) {

            clearDictTypeCache(dict.getDictId());
            dictDataService.clearDictDataCache(oldDict.getDictType());
            dictDataService.clearDictDataCache(dict.getDictType());
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
