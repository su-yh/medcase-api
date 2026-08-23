package com.ruoyi.biz.controller;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.biz.request.DoctorCaseReviewQuery;
import com.ruoyi.biz.request.DoctorCaseReviewRequest;
import com.ruoyi.biz.response.DoctorCaseReviewVO;
import com.ruoyi.mp.mybatis.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DoctorCaseReviewAdminControllerTest {
    @Test
    void reviewControllerSharesCaseInfoPackageAndUsesNewResponseTypes() throws NoSuchMethodException {
        RequestMapping mapping = DoctorCaseReviewAdminController.class.getAnnotation(RequestMapping.class);
        Method list = DoctorCaseReviewAdminController.class.getMethod(
                "list", Integer.class, Integer.class, DoctorCaseReviewQuery.class);
        Method detail = DoctorCaseReviewAdminController.class.getMethod("getInfo", Long.class);
        Method review = DoctorCaseReviewAdminController.class.getMethod(
                "review", LoginUser.class, Long.class, DoctorCaseReviewRequest.class);
        Method settle = DoctorCaseReviewAdminController.class.getMethod(
                "settle", LoginUser.class, Long.class);

        assertEquals("com.ruoyi.biz.controller",
                DoctorCaseReviewAdminController.class.getPackageName());
        assertEquals("/biz/case-review", mapping.value()[0]);
        assertEquals(PageResult.class, list.getReturnType());
        assertEquals(DoctorCaseReviewVO.class, detail.getReturnType());
        assertEquals(void.class, review.getReturnType());
        assertEquals(void.class, settle.getReturnType());
        assertEquals("/{id}/review", review.getAnnotation(PostMapping.class).value()[0]);
        assertEquals("/{id}/settle", settle.getAnnotation(PostMapping.class).value()[0]);
        assertFalse(hasLegacyResponseType(DoctorCaseReviewAdminController.class));
    }

    private boolean hasLegacyResponseType(Class<?> controllerClass) {
        return Arrays.stream(controllerClass.getDeclaredMethods())
                .flatMap(method -> Stream.concat(
                        Stream.of(method.getReturnType()),
                        Arrays.stream(method.getParameterTypes())))
                .anyMatch(type -> type.getName().equals("com.ruoyi.common.core.domain.AjaxResult")
                        || type.getName().equals("com.ruoyi.common.core.page.TableDataInfo"));
    }
}
