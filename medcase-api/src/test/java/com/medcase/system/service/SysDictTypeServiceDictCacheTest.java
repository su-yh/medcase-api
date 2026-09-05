package com.medcase.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.mapper.SysDictDataMapper;
import com.medcase.system.mapper.SysDictTypeMapper;

class SysDictTypeServiceDictCacheTest {

    private static final String DICT_TYPE = "sys_user_sex";
    private static final String OTHER_DICT_TYPE = "sys_user_status";

    private SysDictTypeService dictTypeService;

    @Mock
    private SysDictTypeMapper dictTypeMapper;

    @Mock
    private SysDictDataMapper dictDataMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        dictTypeService = new SysDictTypeService();
        ReflectionTestUtils.setField(dictTypeService, "dictTypeMapper", dictTypeMapper);
        ReflectionTestUtils.setField(dictTypeService, "dictDataMapper", dictDataMapper);
    }

    @Test
    void selectDictDataByTypeLoadsEachDictTypeOnceFromDatabase() {

        SysDictDataEntity dictData = new SysDictDataEntity();
        dictData.setDictType(DICT_TYPE);
        dictData.setDictValue("0");
        dictData.setDictLabel("男");
        when(dictDataMapper.selectEnabledDictDataByType(DICT_TYPE)).thenReturn(List.of(dictData));

        List<SysDictData> firstResult = dictTypeService.selectDictDataByType(DICT_TYPE);
        List<SysDictData> secondResult = dictTypeService.selectDictDataByType(DICT_TYPE);

        assertEquals("男", firstResult.get(0).getDictLabel());
        assertEquals("男", secondResult.get(0).getDictLabel());
        verify(dictDataMapper, times(1)).selectEnabledDictDataByType(DICT_TYPE);
    }

    @Test
    void clearDictCacheInvalidatesOnlySpecifiedDictType() {

        when(dictDataMapper.selectEnabledDictDataByType(DICT_TYPE)).thenReturn(List.of());
        when(dictDataMapper.selectEnabledDictDataByType(OTHER_DICT_TYPE)).thenReturn(List.of());

        dictTypeService.selectDictDataByType(DICT_TYPE);
        dictTypeService.selectDictDataByType(OTHER_DICT_TYPE);
        assertDoesNotThrow(() ->
                ReflectionTestUtils.invokeMethod(dictTypeService, "clearDictCache", DICT_TYPE));
        dictTypeService.selectDictDataByType(DICT_TYPE);
        dictTypeService.selectDictDataByType(OTHER_DICT_TYPE);

        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(DICT_TYPE);
        verify(dictDataMapper, times(1)).selectEnabledDictDataByType(OTHER_DICT_TYPE);
    }

    @Test
    void clearDictCacheInvalidatesAllDictTypes() {

        when(dictDataMapper.selectEnabledDictDataByType(DICT_TYPE)).thenReturn(List.of());
        when(dictDataMapper.selectEnabledDictDataByType(OTHER_DICT_TYPE)).thenReturn(List.of());

        dictTypeService.selectDictDataByType(DICT_TYPE);
        dictTypeService.selectDictDataByType(OTHER_DICT_TYPE);
        dictTypeService.clearDictCache();
        dictTypeService.selectDictDataByType(DICT_TYPE);
        dictTypeService.selectDictDataByType(OTHER_DICT_TYPE);

        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(DICT_TYPE);
        verify(dictDataMapper, times(2)).selectEnabledDictDataByType(OTHER_DICT_TYPE);
    }
}
