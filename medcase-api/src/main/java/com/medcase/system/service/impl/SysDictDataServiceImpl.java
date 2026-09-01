package com.medcase.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.common.utils.DictUtils;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.converter.SystemEntityConverter;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.mapper.SysDictDataMapper;
import com.medcase.system.service.ISysDictDataService;

/**
 * 字典 业务层处理
 * 
 */
@Service
public class SysDictDataServiceImpl implements ISysDictDataService {

    @Autowired
    private SysDictDataMapper dictDataMapper;

    /**
     * 根据条件分页查询字典数据
     * 
     * @param dictData 字典数据信息
     * @return 字典数据集合信息
     */
    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {

        return SystemEntityConverter.copyList(dictDataMapper.selectDictDataList(
                dictData.getDictType(), dictData.getDictLabel(), dictData.getStatus()), SysDictData.class);
    }

    @Override
    public PageResult<SysDictData> selectPage(PageParam pageParam, SysDictData dictData) {

        PageResult<SysDictDataEntity> entityPage = dictDataMapper.selectPage(
                pageParam, dictData.getDictType(), dictData.getDictLabel(), dictData.getStatus());
        PageResult<SysDictData> result = new PageResult<>();
        result.setList(SystemEntityConverter.copyList(entityPage.getList(), SysDictData.class));
        result.setTotal(entityPage.getTotal());
        return result;
    }

    /**
     * 根据字典类型和字典键值查询字典数据信息
     * 
     * @param dictType 字典类型
     * @param dictValue 字典键值
     * @return 字典标签
     */
    @Override
    public String selectDictLabel(String dictType, String dictValue) {

        SysDictDataEntity entity = dictDataMapper.selectDictLabel(dictType, dictValue);
        return entity == null ? null : entity.getDictLabel();
    }

    /**
     * 根据字典数据ID查询信息
     * 
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    @Override
    public SysDictData selectDictDataById(Long dictCode) {

        return SystemEntityConverter.toDomain(dictDataMapper.selectById(dictCode));
    }

    /**
     * 批量删除字典数据信息
     * 
     * @param dictCodes 需要删除的字典数据ID
     */
    @Override
    public void deleteDictDataByIds(Long[] dictCodes) {

        for (Long dictCode : dictCodes) {

            SysDictData data = selectDictDataById(dictCode);
            dictDataMapper.deleteById(dictCode);
            List<SysDictData> dictDatas = selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
    }

    /**
     * 新增保存字典数据信息
     * 
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int insertDictData(SysDictData data) {

        SysDictDataEntity entity = SystemEntityConverter.toEntity(data);
        int row = dictDataMapper.insert(entity);
        data.setDictCode(entity.getDictCode());
        if (row > 0) {

            List<SysDictData> dictDatas = selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    /**
     * 修改保存字典数据信息
     * 
     * @param data 字典数据信息
     * @return 结果
     */
    @Override
    public int updateDictData(SysDictData data) {

        int row = dictDataMapper.updateById(SystemEntityConverter.toEntity(data));
        if (row > 0) {

            List<SysDictData> dictDatas = selectDictDataByType(data.getDictType());
            DictUtils.setDictCache(data.getDictType(), dictDatas);
        }
        return row;
    }

    private List<SysDictData> selectDictDataByType(String dictType) {

        return SystemEntityConverter.copyList(
                dictDataMapper.selectEnabledDictDataByType(dictType),
                SysDictData.class);
    }
}
