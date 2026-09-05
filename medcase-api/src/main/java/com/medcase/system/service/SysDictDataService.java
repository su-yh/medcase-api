package com.medcase.system.service;

import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.mapper.SysDictDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 字典 业务层处理
 * 
 */
@Service
public class SysDictDataService {

    @Autowired
    private SysDictDataMapper dictDataMapper;

    @Autowired
    private SysDictTypeService dictTypeService;

    public PageResult<SysDictData> selectPage(PageParam pageParam, SysDictData dictData) {

        PageResult<SysDictDataEntity> entityPage = dictDataMapper.selectPage(
                pageParam, dictData.getDictType(), dictData.getDictLabel(), dictData.getStatus());
        PageResult<SysDictData> result = new PageResult<>();
        result.setList(SystemEntityConverter.copyList(entityPage.getList(), SysDictData.class));
        result.setTotal(entityPage.getTotal());
        return result;
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
     * 批量删除字典数据信息
     * 
     * @param dictCodes 需要删除的字典数据ID
     */
    public void deleteDictDataByIds(Long[] dictCodes) {

        for (Long dictCode : dictCodes) {

            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteById(dictCode);
            dictTypeService.clearDictCache(data.getDictType());
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

            dictTypeService.clearDictCache(data.getDictType());
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
                dictTypeService.clearDictCache(oldData.getDictType());
            }
            dictTypeService.clearDictCache(data.getDictType());
        }
        return row;
    }
}
