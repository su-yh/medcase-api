package com.ruoyi.common.core.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.ruoyi.common.enums.UserTypeEnums;
import org.junit.jupiter.api.Test;

class SysUserUserTypeTest {

    @Test
    void newUserShouldNotHideMissingUserType() {

        SysUser user = new SysUser();

        assertNull(user.getUserType());
    }

    @Test
    void userTypeShouldBeWritableForFutureUserTypes() {

        SysUser user = new SysUser();

        user.setUserType(UserTypeEnums.DOCTOR);

        assertEquals(UserTypeEnums.DOCTOR, user.getUserType());
    }
}
