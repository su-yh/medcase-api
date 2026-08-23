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

class DoctorCaseReviewControllerTest {
    @Test
    void reviewControllerSharesCaseInfoPackageAndUsesNewResponseTypes() throws NoSuchMethodException {
        RequestMapping mapping = DoctorCaseReviewController.class.getAnnotation(RequestMapping.class);
        Method list = DoctorCaseReviewController.class.getMethod(
                "list", Integer.class, Integer.class, DoctorCaseReviewQuery.class);
        Method detail = DoctorCaseReviewController.class.getMethod("getInfo", Long.class);
        Method review = DoctorCaseReviewController.class.getMethod(
                "review", LoginUser.class, Long.class, DoctorCaseReviewRequest.class);

        assertEquals("com.ruoyi.biz.controller",
                DoctorCaseReviewController.class.getPackageName());
        assertEquals("/biz/case-review", mapping.value()[0]);
        assertEquals(PageResult.class, list.getReturnType());
        assertEquals(DoctorCaseReviewVO.class, detail.getReturnType());
        assertEquals(void.class, review.getReturnType());
        assertEquals("/{id}/review", review.getAnnotation(PostMapping.class).value()[0]);
        assertFalse(hasLegacyResponseType(DoctorCaseReviewController.class));
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
