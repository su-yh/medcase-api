package com.medcase.system.service;

import com.medcase.common.core.domain.entity.SysDictData;
import com.medcase.common.core.domain.entity.SysDictType;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysConfigEntity;
import com.medcase.system.entity.SysNoticeEntity;
import com.medcase.system.entity.SysPostEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemSingleTablePageServiceTest {
    @Test
    void simpleSingleTableServicesExposePageMethods() throws NoSuchMethodException {
        assertPageMethod(SysConfigService.class, SysConfigEntity.class,
                String.class, String.class, String.class, String.class, String.class);
        assertPageMethod(SysDictDataService.class, SysDictData.class, SysDictData.class);
        assertPageMethod(SysDictTypeService.class, SysDictType.class, SysDictType.class);
        assertPageMethod(SysPostService.class, SysPostEntity.class,
                String.class, String.class, String.class);
        assertPageMethod(SysNoticeService.class, SysNoticeEntity.class,
                String.class, String.class, String.class);
    }

    private void assertPageMethod(
            Class<?> serviceType, Class<?> elementType, Class<?>... queryParameterTypes)
            throws NoSuchMethodException {
        Class<?>[] parameterTypes = new Class<?>[queryParameterTypes.length + 1];
        parameterTypes[0] = PageParam.class;
        System.arraycopy(queryParameterTypes, 0, parameterTypes, 1, queryParameterTypes.length);
        Method pageMethod = serviceType.getMethod("selectPage", parameterTypes);

        assertEquals(PageResult.class, pageMethod.getReturnType());
        assertEquals(elementType.getName(), pageMethod.getGenericReturnType()
                .getTypeName()
                .replace("com.medcase.mp.mybatis.PageResult<", "")
                .replace(">", ""));
    }
}
