package com.ruoyi.biz.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserStatusEnums;
import com.ruoyi.common.enums.UserTypeEnums;
import org.junit.jupiter.api.Test;

class DoctorPermissionServiceTest {
    private final DoctorPermissionService doctorPermissionService = new DoctorPermissionService();

    @Test
    void approvedDoctorPasses() {
        assertTrue(doctorPermissionService.hasAnyStatus(
                loginUser(UserTypeEnums.DOCTOR, UserStatusEnums.OK), UserStatusEnums.OK));
    }

    @Test
    void doctorWithoutApprovalFails() {
        assertFalse(doctorPermissionService.hasAnyStatus(
                loginUser(UserTypeEnums.DOCTOR, UserStatusEnums.REGISTER), UserStatusEnums.OK));
    }

    @Test
    void adminFailsEvenWhenStatusIsNormal() {
        assertFalse(doctorPermissionService.hasAnyStatus(
                loginUser(UserTypeEnums.ADMIN, UserStatusEnums.OK), UserStatusEnums.OK));
    }

    @Test
    void doctorPassesWhenAnyAllowedStatusMatches() {
        assertTrue(doctorPermissionService.hasAnyStatus(
                loginUser(UserTypeEnums.DOCTOR, UserStatusEnums.REVIEW_FAILED),
                UserStatusEnums.OK, UserStatusEnums.REVIEW_FAILED));
    }

    private LoginUser loginUser(UserTypeEnums userType, UserStatusEnums status) {
        SysUser user = new SysUser();
        user.setUserType(userType);
        user.setStatus(status.getCode());

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        return loginUser;
    }
}
