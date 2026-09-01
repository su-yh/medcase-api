package com.medcase.web.controller.monitor;

import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.web.controller.monitor.dto.LogininforQueryRequest;
import com.medcase.web.controller.monitor.dto.LogininforResponse;
import com.medcase.web.controller.monitor.dto.OperLogQueryRequest;
import com.medcase.web.controller.monitor.dto.OperLogResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemMonitorPageControllerTest {
    @Test
    void monitorListControllersShouldUsePageParam() throws NoSuchMethodException {
        assertListMethod(SysLogininforController.class, LogininforQueryRequest.class, LogininforResponse.class);
        assertListMethod(SysOperlogController.class, OperLogQueryRequest.class, OperLogResponse.class);
    }

    private void assertListMethod(
            Class<?> controllerType, Class<?> queryType, Class<?> responseType) throws NoSuchMethodException {
        Method method = controllerType.getMethod("list", PageParam.class, queryType);
        assertEquals(PageResult.class, method.getReturnType());
        ParameterizedType pageResultType = (ParameterizedType) method.getGenericReturnType();
        assertEquals(responseType.getTypeName(), pageResultType.getActualTypeArguments()[0].getTypeName());
    }
}
