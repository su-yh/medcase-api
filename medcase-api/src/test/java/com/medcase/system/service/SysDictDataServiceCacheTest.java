package com.medcase.system.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.mapper.SysDictDataMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class SysDictDataServiceCacheTest {

    private static final Long DICT_CODE = 1L;
    private static final String OLD_DICT_TYPE = "old_type";
    private static final String NEW_DICT_TYPE = "new_type";

    private SysDictDataService dictDataService;

    @Mock
    private SysDictDataMapper dictDataMapper;

    @Mock
    private SysDictTypeService dictTypeService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        dictDataService = new SysDictDataService();
        ReflectionTestUtils.setField(dictDataService, "dictDataMapper", dictDataMapper);
        ReflectionTestUtils.setField(dictDataService, "dictTypeService", dictTypeService);
    }

    @Test
    void insertDictDataClearsTheAffectedDictTypeCache() {

        SysDictData data = new SysDictData();
        data.setDictType(NEW_DICT_TYPE);
        when(dictDataMapper.insert(any(SysDictDataEntity.class))).thenReturn(1);

        dictDataService.insertDictData(data);

        verify(dictTypeService).clearDictCache(NEW_DICT_TYPE);
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

        dictDataService.updateDictData(data);

        verify(dictTypeService).clearDictCache(OLD_DICT_TYPE);
        verify(dictTypeService).clearDictCache(NEW_DICT_TYPE);
    }

    @Test
    void deleteDictDataClearsTheDeletedDictTypeCache() {

        SysDictDataEntity data = new SysDictDataEntity();
        data.setDictType(OLD_DICT_TYPE);
        when(dictDataMapper.selectById(DICT_CODE)).thenReturn(data);
        when(dictDataMapper.deleteById(DICT_CODE)).thenReturn(1);

        dictDataService.deleteDictDataByIds(new Long[]{DICT_CODE});

        verify(dictTypeService, times(1)).clearDictCache(OLD_DICT_TYPE);
    }
}
