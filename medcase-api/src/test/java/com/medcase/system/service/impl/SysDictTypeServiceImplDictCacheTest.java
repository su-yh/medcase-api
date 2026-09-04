package com.medcase.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import com.medcase.common.constant.CacheConstants;
import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.common.core.redis.RedisCache;
import com.medcase.common.utils.json.JsonUtils;
import com.medcase.system.mapper.SysDictDataMapper;
import com.medcase.system.mapper.SysDictTypeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

class SysDictTypeServiceImplDictCacheTest {

    private static final String DICT_TYPE = "sys_user_sex";

    private SysDictTypeServiceImpl dictTypeService;

    @Mock
    private SysDictTypeMapper dictTypeMapper;

    @Mock
    private SysDictDataMapper dictDataMapper;

    @Mock
    private RedisCache redisCache;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);
        dictTypeService = new SysDictTypeServiceImpl();
        ReflectionTestUtils.setField(dictTypeService, "dictTypeMapper", dictTypeMapper);
        ReflectionTestUtils.setField(dictTypeService, "dictDataMapper", dictDataMapper);
        ReflectionTestUtils.setField(dictTypeService, "redisCache", redisCache);
    }

    @Test
    void selectDictDataByTypeReadsJsonFromRedisCache() {

        SysDictData dictData = new SysDictData();
        dictData.setDictType(DICT_TYPE);
        dictData.setDictValue("0");
        dictData.setDictLabel("男");
        String cacheKey = CacheConstants.SYS_DICT_KEY + DICT_TYPE;
        when(redisCache.getCacheObject(cacheKey)).thenReturn(JsonUtils.toJSONString(List.of(dictData)));

        List<SysDictData> result = dictTypeService.selectDictDataByType(DICT_TYPE);

        assertEquals("男", result.get(0).getDictLabel());
        verifyNoInteractions(dictDataMapper);
    }

    @Test
    void clearDictCacheDeletesKeysFromRedisCache() {

        List<String> cacheKeys = List.of(CacheConstants.SYS_DICT_KEY + DICT_TYPE);
        when(redisCache.keys(CacheConstants.SYS_DICT_KEY + "*")).thenReturn(cacheKeys);

        dictTypeService.clearDictCache();

        verify(redisCache).deleteObject(cacheKeys);
    }
}
