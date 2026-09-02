package com.medcase.biz.controller;

import com.medcase.biz.request.CaseReviewQuery;
import com.medcase.biz.request.CaseReviewRequest;
import com.medcase.biz.response.CaseReviewVO;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mp.mybatis.PageParam;
import com.medcase.mp.mybatis.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientCaseReviewAdminControllerTest {
    @Test
    void listUsesUnifiedPageParameter() throws NoSuchMethodException {
        RequestMapping mapping = PatientCaseReviewAdminController.class.getAnnotation(RequestMapping.class);
        Method list = PatientCaseReviewAdminController.class.getMethod(
                "list", PageParam.class, CaseReviewQuery.class);
        Method review = PatientCaseReviewAdminController.class.getMethod(
                "review", LoginUser.class, Long.class, CaseReviewRequest.class);

        assertEquals("/biz/patient-case", mapping.value()[0]);
        assertEquals(PageResult.class, list.getReturnType());
        assertEquals(void.class, review.getReturnType());
    }
}
