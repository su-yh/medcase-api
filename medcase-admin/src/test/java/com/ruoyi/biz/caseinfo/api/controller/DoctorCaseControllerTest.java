package com.ruoyi.biz.caseinfo.api.controller;

import com.ruoyi.biz.caseinfo.service.DoctorCaseService;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoctorCaseControllerTest {
    @Test
    void caseControllerUsesBizDoctorRoute() {
        RequestMapping mapping = DoctorCaseController.class.getAnnotation(RequestMapping.class);

        assertEquals("/biz/doctor", mapping.value()[0]);
    }
}
