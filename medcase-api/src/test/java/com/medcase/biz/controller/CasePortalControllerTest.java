package com.medcase.biz.controller;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class CasePortalControllerTest {
    @Test
    void caseControllerUsesIndependentBizCasesRoute() {
        RequestMapping mapping = CasePortalController.class.getAnnotation(RequestMapping.class);

        assertEquals("com.medcase.biz.controller",
                CasePortalController.class.getPackageName());
        assertArrayEquals(new String[] {"/biz/cases"}, mapping.value());
    }

    @Test
    void caseControllerUsesDoctorClientRoutes() {
        Map<String, RequestMapping> mappings = Arrays.stream(CasePortalController.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .collect(Collectors.toMap(Method::getName,
                        method -> method.getAnnotation(RequestMapping.class)));

        assertEquals("/biz/cases", route(mappings.get("submit")));
        assertEquals("/biz/cases/draft", route(mappings.get("saveDraft")));
        assertEquals("/biz/cases", route(mappings.get("page")));
        assertEquals("/biz/cases/{id}", route(mappings.get("detail")));
        assertEquals("/biz/cases/{id}", route(mappings.get("delete")));
    }

    @Test
    void caseControllerRequiresApprovedDoctor() {
        Arrays.stream(CasePortalController.class.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(RequestMapping.class))
                .forEach(method -> {
                    PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);
                    assertEquals(
                            "@dp.hasAnyStatus(#user, T(com.medcase.common.enums.UserStatusEnums).OK)",
                            preAuthorize.value());
                });
    }

    private String route(RequestMapping mapping) {
        return "/biz/cases" + (mapping.value().length == 0 ? "" : mapping.value()[0]);
    }
}
