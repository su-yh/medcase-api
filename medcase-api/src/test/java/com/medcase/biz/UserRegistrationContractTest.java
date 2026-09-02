package com.medcase.biz;

import com.medcase.biz.request.UserProfileSubmitRequest;
import com.medcase.biz.request.UserRegisterRequest;
import com.medcase.storage.enums.FileBusinessEnums;
import com.medcase.web.controller.file.FileStorageController;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRegistrationContractTest {
    @Test
    void registrationRequestContainsOnlyAccountFields() {
        Set<String> fields = Arrays.stream(UserRegisterRequest.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        assertEquals(Set.of("userType", "username", "password", "phone", "smsCode"), fields);
    }

    @Test
    void profileRequestContainsSupplierIdAndDoesNotContainSex() throws NoSuchFieldException {
        assertEquals(Long.class, UserProfileSubmitRequest.class
                .getDeclaredField("supplierId").getType());
        assertFalse(Arrays.stream(UserProfileSubmitRequest.class.getDeclaredFields())
                .map(Field::getName)
                .anyMatch("sex"::equals));
    }

    @Test
    void anonymousCaseRegistrationUploadIsRemoved() {
        assertFalse(Arrays.stream(FileStorageController.class.getDeclaredMethods())
                .map(Method::getName)
                .anyMatch("uploadCaseRegistration"::equals));
        assertTrue(Arrays.stream(FileBusinessEnums.values())
                .noneMatch(business -> business.name().equals("CASE_REGISTER")));
    }
}
