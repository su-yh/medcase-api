package com.medcase.biz.request;

import com.medcase.common.enums.UserSexEnums;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SupplierSaveRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validatesEnumSexWithNotNull() {
        SupplierSaveRequest request = new SupplierSaveRequest();
        request.setName("苏云弘");
        request.setSex(UserSexEnums.MALE);
        request.setPhone("17727448330");
        request.setEmail("");
        request.setIdCardNumber("500384198511152419");
        request.setStatus(Boolean.TRUE);
        request.setRemark("");

        assertTrue(validator.validate(request).isEmpty());
    }
}
