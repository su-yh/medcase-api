package com.ruoyi.biz.controller;

import com.ruoyi.biz.request.DoctorUserQuery;
import com.ruoyi.biz.response.DoctorUserVO;
import com.ruoyi.mp.mybatis.PageResult;
import com.ruoyi.mp.mybatis.PageParam;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DoctorUserControllerTest {
    @Test
    void usesNewPageAndDetailResponseTypes() throws NoSuchMethodException {
        RequestMapping mapping = DoctorUserController.class.getAnnotation(RequestMapping.class);
        Method list = DoctorUserController.class.getMethod(
                "list", PageParam.class, DoctorUserQuery.class);
        Method detail = DoctorUserController.class.getMethod("getInfo", Long.class);

        assertEquals("/biz/doctor-user", mapping.value()[0]);
        assertEquals(PageResult.class, list.getReturnType());
        assertEquals(DoctorUserVO.class, detail.getReturnType());
        assertFalse(DoctorUserController.class.getSuperclass().getName()
                .equals("com.ruoyi.common.core.controller.BaseController"));
    }
}
