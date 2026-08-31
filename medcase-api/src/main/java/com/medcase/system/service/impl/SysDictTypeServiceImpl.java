package com.medcase.system.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medcase.common.constant.UserConstants;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.common.core.domain.entity.SysDictType;
import com.medcase.common.utils.DictUtils;
import com.medcase.common.utils.StringUtils;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysDictTypeEntity;
import com.medcase.system.mapper.SysDictDataMapper;
import com.medcase.system.mapper.SysDictTypeMapper;
import com.medcase.system.service.ISysDictTypeService;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;

/**
 * 字典 业务层处理
 * 
 */
@Service
public class SysDictTypeServiceImpl implements ISysDictTypeService {

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

    /**
     * 根据条件分页查询字典类型
     * 
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {

        Object beginTime = dictType.getParams().get("beginTime");
        Object endTime = dictType.getParams().get("endTime");
        return SystemEntityConverter.copyList(dictTypeMapper.selectDictTypeList(
                dictType.getDictName(), dictType.getStatus(), dictType.getDictType(),
                beginTime, endTime), SysDictType.class);
    }

    /**
     * 根据所有字典类型
     * 
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictType> selectDictTypeAll() {

        return SystemEntityConverter.copyList(dictTypeMapper.selectAllDictTypes(), SysDictType.class);
    }

    /**
     * 根据字典类型查询字典数据
     * 
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataByType(String dictType) {

        List<SysDictData> dictDatas = DictUtils.getDictCache(dictType);
        if (StringUtils.isNotEmpty(dictDatas)) {

            return dictDatas;
        }
        dictDatas = SystemEntityConverter.copyList(
                dictDataMapper.selectEnabledDictDataByType(dictType),
                SysDictData.class);
        if (StringUtils.isNotEmpty(dictDatas)) {

            DictUtils.setDictCache(dictType, dictDatas);
            return dictDatas;
        }
        return null;
    }

    /**
     * 根据字典类型ID查询信息
     * 
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeById(Long dictId) {

        return SystemEntityConverter.toDomain(dictTypeMapper.selectById(dictId));
    }

    /**
     * 根据字典类型查询信息
     * 
     * @param dictType 字典类型
     * @return 字典类型
     */
    @Override
    public SysDictType selectDictTypeByType(String dictType) {

        return SystemEntityConverter.toDomain(dictTypeMapper.selectDictTypeByType(dictType));
    }

    /**
     * 批量删除字典类型信息
     * 
     * @param dictIds 需要删除的字典ID
     */
    @Override
    public void deleteDictTypeByIds(Long[] dictIds) {

        for (Long dictId : dictIds) {

            SysDictType dictType = selectDictTypeById(dictId);
            if (dictDataMapper.countByDictType(dictType.getDictType()) > 0) {

                throw ExceptionUtil.business(ErrorCodeEnums.DICT_TYPE_ASSIGNED_DELETE, dictType.getDictName());
            }
            dictTypeMapper.deleteById(dictId);
            DictUtils.removeDictCache(dictType.getDictType());
        }
    }

    /**
     * 加载字典缓存数据
     */
    @Override
    public void loadingDictCache() {

        SysDictData dictData = new SysDictData();
        dictData.setStatus("0");
        Map<String, List<SysDictData>> dictDataMap = SystemEntityConverter.copyList(
                dictDataMapper.selectDictDataList(
                        dictData.getDictType(), dictData.getDictLabel(), dictData.getStatus()),
                SysDictData.class).stream().collect(Collectors.groupingBy(SysDictData::getDictType));
        for (Map.Entry<String, List<SysDictData>> entry : dictDataMap.entrySet()) {

            DictUtils.setDictCache(entry.getKey(), entry.getValue().stream().sorted(Comparator.comparing(SysDictData::getDictSort)).collect(Collectors.toList()));
        }
    }

    /**
     * 清空字典缓存数据
     */
    @Override
    public void clearDictCache() {

        DictUtils.clearDictCache();
    }

    /**
     * 重置字典缓存数据
     */
    @Override
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
    @Override
    public int insertDictType(SysDictType dict) {

        SysDictTypeEntity entity = SystemEntityConverter.toEntity(dict);
        int row = dictTypeMapper.insert(entity);
        dict.setDictId(entity.getDictId());
        if (row > 0) {

            DictUtils.setDictCache(dict.getDictType(), null);
        }
        return row;
    }

    /**
     * 修改保存字典类型信息
     * 
     * @param dict 字典类型信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDictType(SysDictType dict) {

        SysDictType oldDict = selectDictTypeById(dict.getDictId());
        dictDataMapper.updateDictType(oldDict.getDictType(), dict.getDictType());
        int row = dictTypeMapper.updateById(SystemEntityConverter.toEntity(dict));
        if (row > 0) {

            List<SysDictData> dictDatas = selectDictDataByType(dict.getDictType());
            DictUtils.setDictCache(dict.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 校验字典类型称是否唯一
     * 
     * @param dict 字典类型
     * @return 结果
     */
    @Override
    public boolean checkDictTypeUnique(SysDictType dict) {

        Long dictId = StringUtils.isNull(dict.getDictId()) ? -1L : dict.getDictId();
        SysDictType dictType = SystemEntityConverter.toDomain(
                dictTypeMapper.selectDictTypeByType(dict.getDictType()));
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId.longValue()) {

            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}
