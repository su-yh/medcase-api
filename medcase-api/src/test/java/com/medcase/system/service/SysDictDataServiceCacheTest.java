package com.medcase.system.service;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.mapper.SysDictDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class SysDictDataServiceCacheTest {

    private static final String DICT_TYPE = "sys_user_sex";
    private static final String OTHER_DICT_TYPE = "sys_user_status";
    private static final Long DICT_CODE = 1L;
    private static final String OLD_DICT_TYPE = "old_type";
    private static final String NEW_DICT_TYPE = "new_type";

    private SysDictDataService dictDataService;

    @Mock
    private SysDictDataMapper dictDataMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        dictDataService = new SysDictDataService();
        ReflectionTestUtils.setField(dictDataService, "dictDataMapper", dictDataMapper);
    }

    @Test
    void selectDictDataByTypeLoadsEachDictTypeOnceFromDatabase() {

        SysDictDataEntity dictData = new SysDictDataEntity();
        dictData.setDictType(DICT_TYPE);
        dictData.setDictValue("0");
        dictData.setDictLabel("男");
        when(dictDataMapper.selectEnabledDictDataByType(DICT_TYPE)).thenReturn(List.of(dictData));

        List<SysDictData> firstResult = dictDataService.selectDictDataByType(DICT_TYPE);
        List<SysDictData> secondResult = dictDataService.selectDictDataByType(DICT_TYPE);

        assertEquals("男", firstResult.get(0).getDictLabel());
        assertEquals("男", secondResult.get(0).getDictLabel());
        verify(dictDataMapper, times(1)).selectEnabledDictDataByType(DICT_TYPE);
    }

    @Test
    void clearDictDataCacheInvalidatesOnlySpecifiedDictType() {

        when(dictDataMapper.selectEnabledDictDataByType(DICT_TYPE)).thenReturn(List.of(), List.of());
        when(dictDataMapper.selectEnabledDictDataByType(OTHER_DICT_TYPE)).thenReturn(List.of());

        dictDataService.selectDictDataByType(DICT_TYPE);
        dictDataService.selectDictDataByType(OTHER_DICT_TYPE);
        dictDataService.clearDictDataCache(DICT_TYPE);
        dictDataService.selectDictDataByType(DICT_TYPE);
        dictDataService.selectDictDataByType(OTHER_DICT_TYPE);

        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(DICT_TYPE);
        verify(dictDataMapper, times(1)).selectEnabledDictDataByType(OTHER_DICT_TYPE);
    }

    @Test
    void clearDictCacheInvalidatesAllDictTypes() {

        when(dictDataMapper.selectEnabledDictDataByType(DICT_TYPE)).thenReturn(List.of());
        when(dictDataMapper.selectEnabledDictDataByType(OTHER_DICT_TYPE)).thenReturn(List.of());

        dictDataService.selectDictDataByType(DICT_TYPE);
        dictDataService.selectDictDataByType(OTHER_DICT_TYPE);
        dictDataService.clearDictCache();
        dictDataService.selectDictDataByType(DICT_TYPE);
        dictDataService.selectDictDataByType(OTHER_DICT_TYPE);

        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(DICT_TYPE);
        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(OTHER_DICT_TYPE);
    }

    @Test
    void insertDictDataClearsTheAffectedDictTypeCache() {

        SysDictData data = new SysDictData();
        data.setDictType(NEW_DICT_TYPE);
        when(dictDataMapper.insert(any(SysDictDataEntity.class))).thenReturn(1);
        when(dictDataMapper.selectEnabledDictDataByType(NEW_DICT_TYPE)).thenReturn(List.of(), List.of());

        dictDataService.selectDictDataByType(NEW_DICT_TYPE);
        dictDataService.insertDictData(data);
        dictDataService.selectDictDataByType(NEW_DICT_TYPE);

        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(NEW_DICT_TYPE);
    }

    @Test
    void updateDictDataClearsBothOldAndNewDictTypeCaches() {

        SysDictDataEntity oldData = new SysDictDataEntity();
        oldData.setDictType(OLD_DICT_TYPE);
        SysDictData data = new SysDictData();
        data.setDictCode(DICT_CODE);
        data.setDictType(NEW_DICT_TYPE);
        when(dictDataMapper.selectById(DICT_CODE)).thenReturn(oldData);
        when(dictDataMapper.updateById(any(SysDictDataEntity.class))).thenReturn(1);
        when(dictDataMapper.selectEnabledDictDataByType(OLD_DICT_TYPE)).thenReturn(List.of(), List.of());
        when(dictDataMapper.selectEnabledDictDataByType(NEW_DICT_TYPE)).thenReturn(List.of(), List.of());

        dictDataService.selectDictDataByType(OLD_DICT_TYPE);
        dictDataService.selectDictDataByType(NEW_DICT_TYPE);
        dictDataService.updateDictData(data);
        dictDataService.selectDictDataByType(OLD_DICT_TYPE);
        dictDataService.selectDictDataByType(NEW_DICT_TYPE);

        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(OLD_DICT_TYPE);
        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(NEW_DICT_TYPE);
    }

    @Test
    void deleteDictDataClearsTheDeletedDictTypeCache() {

        SysDictDataEntity data = new SysDictDataEntity();
        data.setDictType(OLD_DICT_TYPE);
        when(dictDataMapper.selectById(DICT_CODE)).thenReturn(data);
        when(dictDataMapper.deleteById(DICT_CODE)).thenReturn(1);
        when(dictDataMapper.selectEnabledDictDataByType(OLD_DICT_TYPE)).thenReturn(List.of(), List.of());

        dictDataService.selectDictDataByType(OLD_DICT_TYPE);
        dictDataService.deleteDictDataByIds(new Long[]{DICT_CODE});
        dictDataService.selectDictDataByType(OLD_DICT_TYPE);

        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(OLD_DICT_TYPE);
    }
}
