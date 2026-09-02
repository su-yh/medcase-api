package com.medcase.biz.controller;

import com.medcase.biz.request.UserQuery;
import com.medcase.biz.request.UserReviewRequest;
import com.medcase.biz.response.UserVO;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mp.mybatis.PageParam;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DoctorUserAdminControllerTest {
    @Test
    void usesNewPageAndDetailResponseTypes() throws NoSuchMethodException {
        RequestMapping mapping = DoctorUserAdminController.class.getAnnotation(RequestMapping.class);
        Method list = DoctorUserAdminController.class.getMethod(
                "list", PageParam.class, UserQuery.class);
        Method detail = DoctorUserAdminController.class.getMethod("getInfo", Long.class);
        Method review = DoctorUserAdminController.class.getMethod(
                "review", Long.class, UserReviewRequest.class);

        assertEquals("/biz/doctor-user", mapping.value()[0]);
        assertEquals(PageResult.class, list.getReturnType());
        assertEquals(UserVO.class, detail.getReturnType());
        assertEquals(void.class, review.getReturnType());
        assertFalse(DoctorUserAdminController.class.getSuperclass().getName()
                .equals("com.medcase.common.core.controller.BaseController"));
    }
}
