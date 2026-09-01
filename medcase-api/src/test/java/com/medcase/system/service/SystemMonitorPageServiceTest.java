package com.medcase.system.service;

import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysLogininforEntity;
import com.medcase.system.entity.SysOperLogEntity;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemMonitorPageServiceTest {
    @Test
    void monitorServicesShouldUsePageParam() throws NoSuchMethodException {
        assertPageMethod(ISysLogininforService.class, SysLogininforEntity.class,
                String.class, String.class, String.class, String.class, String.class);
        assertPageMethod(ISysOperLogService.class, SysOperLogEntity.class,
                String.class, String.class, Integer.class, Integer.class, String.class, String.class, String.class);
    }

    private void assertPageMethod(
            Class<?> serviceType, Class<?> responseType, Class<?>... extraTypes) throws NoSuchMethodException {
        Class<?>[] parameterTypes = new Class<?>[extraTypes.length + 1];
        parameterTypes[0] = PageParam.class;
        System.arraycopy(extraTypes, 0, parameterTypes, 1, extraTypes.length);
        Method method = serviceType.getMethod("selectPage", parameterTypes);
        assertEquals(PageResult.class, method.getReturnType());
        assertEquals(responseType.getName(),
                method.getGenericReturnType().getTypeName()
                        .replace("com.medcase.mp.mybatis.PageResult<", "")
                        .replace(">", ""));
    }
}
