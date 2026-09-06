package com.medcase.common.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.medcase.common.enums.UserTypeEnums;
import com.medcase.system.entity.SysUserEntity;
import org.junit.jupiter.api.Test;

class SysUserUserTypeTest {

    @Test
    void newUserShouldNotHideMissingUserType() {

        SysUserEntity user = new SysUserEntity();

        assertNull(user.getUserType());
    }

    @Test
    void userTypeShouldBeWritableForFutureUserTypes() {

        SysUserEntity user = new SysUserEntity();

        user.setUserType(UserTypeEnums.DOCTOR);

        assertEquals(UserTypeEnums.DOCTOR, user.getUserType());
    }
}
