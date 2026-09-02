package com.medcase.biz.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.enums.UserTypeEnums;
import org.junit.jupiter.api.Test;

class UserPermissionServiceTest {
    private final UserPermissionService userPermissionService = new UserPermissionService();

    @Test
    void approvedDoctorPasses() {
        assertTrue(userPermissionService.hasAnyStatus(
                loginUser(UserTypeEnums.DOCTOR, UserStatusEnums.OK), UserStatusEnums.OK));
    }

    @Test
    void approvedPatientPasses() {
        assertTrue(userPermissionService.hasAnyStatus(
                loginUser(UserTypeEnums.PATIENT, UserStatusEnums.OK), UserStatusEnums.OK));
    }

    @Test
    void doctorWithoutApprovalFails() {
        assertFalse(userPermissionService.hasAnyStatus(
                loginUser(UserTypeEnums.DOCTOR, UserStatusEnums.REGISTER), UserStatusEnums.OK));
    }

    @Test
    void adminFailsEvenWhenStatusIsNormal() {
        assertFalse(userPermissionService.hasAnyStatus(
                loginUser(UserTypeEnums.ADMIN, UserStatusEnums.OK), UserStatusEnums.OK));
    }

    @Test
    void doctorPassesWhenAnyAllowedStatusMatches() {
        assertTrue(userPermissionService.hasAnyStatus(
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
