package com.medcase.framework.web.service;

import com.medcase.common.core.domain.entity.SysDept;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.system.service.SysUserService;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {
    @Test
    void loadUserByUsernameUsesAdminUserType() throws Exception {
        SysUserService userService = Mockito.mock(SysUserService.class);
        when(userService.selectUserByUserName("suyunhong", UserTypeEnums.ADMIN.getCode()))
                .thenReturn(adminUser());

        UserDetailsServiceImpl service = new UserDetailsServiceImpl();
        setField(service, "userService", userService);
        setField(service, "permissionService", new SysPermissionService());

        UserDetails userDetails = service.loadUserByUsername("suyunhong");

        assertInstanceOf(LoginUser.class, userDetails);
        verify(userService).selectUserByUserName("suyunhong", UserTypeEnums.ADMIN.getCode());
        assertSame(UserTypeEnums.ADMIN, ((LoginUser) userDetails).getUser().getUserType());
    }

    private static SysUser adminUser() {
        SysDept dept = new SysDept();
        dept.setDeptId(1L);
        dept.setDeptName("管理部");

        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("suyunhong");
        user.setUserType(UserTypeEnums.ADMIN);
        user.setPassword("$2a$10$7EqJtq98hPqEX7fNZaFWoOHiQ8XOBi9Yx8mD1GmM0l0H8D5vQK0F5S");
        user.setDept(dept);
        user.setRoles(List.of());
        return user;
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
