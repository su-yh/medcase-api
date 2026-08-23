package com.ruoyi.biz.controller;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoctorCasePortalControllerTest {
    @Test
    void caseControllerUsesIndependentBizCasesRoute() {
        RequestMapping mapping = DoctorCasePortalController.class.getAnnotation(RequestMapping.class);

        assertEquals("com.ruoyi.biz.controller",
                DoctorCasePortalController.class.getPackageName());
        assertEquals("/biz/cases", mapping.value()[0]);
    }

    @Test
    void caseControllerUsesDoctorClientRoutes() {
        Map<String, RequestMapping> mappings = Arrays.stream(DoctorCasePortalController.class.getDeclaredMethods())
                .collect(Collectors.toMap(Method::getName,
                        method -> method.getAnnotation(RequestMapping.class)));

        assertEquals("/biz/cases", route(mappings.get("submit")));
        assertEquals("/biz/cases/draft", route(mappings.get("saveDraft")));
        assertEquals("/biz/cases", route(mappings.get("page")));
        assertEquals("/biz/cases/{id}", route(mappings.get("detail")));
        assertEquals("/biz/cases/{id}", route(mappings.get("delete")));
    }

    private String route(RequestMapping mapping) {
        return "/biz/cases" + (mapping.value().length == 0 ? "" : mapping.value()[0]);
    }
}
