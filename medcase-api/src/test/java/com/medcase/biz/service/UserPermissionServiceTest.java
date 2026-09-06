package com.medcase.biz.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.medcase.system.entity.SysUserEntity;
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

    @Test
    void adminMatchesAdminUserType() {
        assertTrue(userPermissionService.hasAnyUserType(
                loginUser(UserTypeEnums.ADMIN, UserStatusEnums.OK), UserTypeEnums.ADMIN));
    }

    @Test
    void portalUserMatchesAnyPortalUserType() {
        assertTrue(userPermissionService.hasAnyUserType(
                loginUser(UserTypeEnums.PATIENT, UserStatusEnums.OK),
                UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT));
    }

    @Test
    void portalUserDoesNotMatchAdminUserType() {
        assertFalse(userPermissionService.hasAnyUserType(
                loginUser(UserTypeEnums.PATIENT, UserStatusEnums.OK), UserTypeEnums.ADMIN));
    }

    private LoginUser loginUser(UserTypeEnums userType, UserStatusEnums status) {
        SysUserEntity user = new SysUserEntity();
        user.setUserType(userType);
        user.setStatus(status.getCode());

        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        return loginUser;
    }
}
