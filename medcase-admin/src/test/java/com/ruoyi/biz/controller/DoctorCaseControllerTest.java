package com.ruoyi.biz.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoctorCaseControllerTest {
    @Test
    void caseControllerUsesIndependentBizCasesRoute() {
        RequestMapping mapping = DoctorCaseController.class.getAnnotation(RequestMapping.class);

        assertEquals("com.ruoyi.biz.controller",
                DoctorCaseController.class.getPackageName());
        assertEquals("/biz/cases", mapping.value()[0]);
    }
}
