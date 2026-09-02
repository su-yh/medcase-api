package com.medcase.common.enums;

import com.medcase.biz.request.UserRegisterRequest;
import com.medcase.common.utils.json.JsonUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserTypeEnumsCasePortalTest {
    @Test
    void supportsPatientCodeAndDescription() {
        assertEquals("02", UserTypeEnums.PATIENT.getCode());
        assertEquals("患者", UserTypeEnums.PATIENT.getDesc());
    }

    @Test
    void bindsRegisterUserTypeFromJsonCode() throws NoSuchFieldException {
        UserRegisterRequest request = JsonUtils.parseObject(
                "{\"userType\":\"01\"}", UserRegisterRequest.class);

        assertEquals(UserTypeEnums.class,
                UserRegisterRequest.class.getDeclaredField("userType").getType());
        assertEquals(UserTypeEnums.DOCTOR, request.getUserType());
    }
}
