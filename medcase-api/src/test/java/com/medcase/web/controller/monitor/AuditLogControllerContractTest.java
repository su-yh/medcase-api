package com.medcase.web.controller.monitor;

import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.web.controller.monitor.dto.AuditLogQueryRequest;
import com.medcase.web.controller.monitor.dto.AuditLogResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class AuditLogControllerContractTest {
    @Test
    void listShouldUseAuditLogContract() throws NoSuchMethodException {
        Method method = AuditLogController.class.getMethod(
                "list", PageParam.class, AuditLogQueryRequest.class);

        assertEquals(PageResult.class, method.getReturnType());
        ParameterizedType pageResultType = (ParameterizedType) method.getGenericReturnType();
        assertEquals(AuditLogResponse.class, pageResultType.getActualTypeArguments()[0]);
    }

    @Test
    void auditLogShouldNotExposeMutationEndpoints() {
        for (Method method : AuditLogController.class.getDeclaredMethods()) {
            assertFalse(method.getName().equals("remove"));
            assertFalse(method.getName().equals("clean"));
        }
    }
}
