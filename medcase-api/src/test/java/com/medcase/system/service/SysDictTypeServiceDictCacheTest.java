package com.medcase.system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import com.medcase.system.entity.SysDictTypeEntity;
import com.medcase.system.mapper.SysDictTypeMapper;

class SysDictTypeServiceDictCacheTest {

    private static final String DICT_TYPE = "sys_user_sex";
    private static final Long DICT_ID = 1L;

    private SysDictTypeService dictTypeService;

    @Mock
    private SysDictTypeMapper dictTypeMapper;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        dictTypeService = new SysDictTypeService();
        ReflectionTestUtils.setField(dictTypeService, "dictTypeMapper", dictTypeMapper);
    }

    @Test
    void selectDictTypeByIdLoadsEachDictTypeOnceFromDatabase() {

        SysDictTypeEntity dictType = new SysDictTypeEntity();
        dictType.setDictId(DICT_ID);
        dictType.setDictType(DICT_TYPE);
        dictType.setDictName("用户性别");
        when(dictTypeMapper.selectById(DICT_ID)).thenReturn(dictType);

        SysDictTypeEntity firstResult = dictTypeService.selectDictTypeById(DICT_ID);
        SysDictTypeEntity secondResult = dictTypeService.selectDictTypeById(DICT_ID);

        assertEquals("用户性别", firstResult.getDictName());
        assertEquals("用户性别", secondResult.getDictName());
    }
}
