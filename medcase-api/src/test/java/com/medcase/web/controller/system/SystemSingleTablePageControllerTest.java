package com.medcase.web.controller.system;

import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.system.entity.SysDictDataEntity;
import com.medcase.system.entity.SysDictTypeEntity;
import com.medcase.web.controller.system.dto.ConfigQueryRequest;
import com.medcase.web.controller.system.dto.ConfigResponse;
import com.medcase.web.controller.system.dto.DictDataQueryRequest;
import com.medcase.web.controller.system.dto.DictTypeQueryRequest;
import com.medcase.web.controller.system.dto.NoticeQueryRequest;
import com.medcase.web.controller.system.dto.NoticeResponse;
import com.medcase.web.controller.system.dto.PostQueryRequest;
import com.medcase.web.controller.system.dto.PostResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SystemSingleTablePageControllerTest {
    @Test
    void simpleSingleTableListsUsePageParam() throws NoSuchMethodException {
        assertListMethod(SysConfigController.class, ConfigResponse.class, ConfigQueryRequest.class);
        assertListMethod(SysDictDataController.class, SysDictDataEntity.class, DictDataQueryRequest.class);
        assertListMethod(SysDictTypeController.class, SysDictTypeEntity.class, DictTypeQueryRequest.class);
        assertListMethod(SysPostController.class, PostResponse.class, PostQueryRequest.class);
        assertListMethod(SysNoticeController.class, NoticeResponse.class, NoticeQueryRequest.class);
    }

    private void assertListMethod(
            Class<?> controllerClass, Class<?> responseType, Class<?> queryType) throws NoSuchMethodException {
        Method listMethod = controllerClass.getMethod("list", PageParam.class, queryType);

        assertEquals(PageResult.class, listMethod.getReturnType());
        ParameterizedType returnType = (ParameterizedType) listMethod.getGenericReturnType();
        assertEquals(responseType, returnType.getActualTypeArguments()[0]);
    }
}
