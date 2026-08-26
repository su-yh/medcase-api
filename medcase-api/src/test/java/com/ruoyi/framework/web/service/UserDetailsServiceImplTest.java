package com.ruoyi.framework.web.service;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.system.service.ISysUserService;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class UserDetailsServiceImplTest {
    @Test
    void loadUserByUsernameUsesAdminUserType() throws Exception {
        AtomicReference<String> usernameRef = new AtomicReference<>();
        AtomicReference<String> userTypeRef = new AtomicReference<>();

        ISysUserService userService = (ISysUserService) Proxy.newProxyInstance(
                ISysUserService.class.getClassLoader(),
                new Class[] {ISysUserService.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) {
                        if ("selectUserByUserName".equals(method.getName())) {
                            usernameRef.set((String) args[0]);
                            userTypeRef.set((String) args[1]);
                            return adminUser();
                        }
                        throw new UnsupportedOperationException(method.getName());
                    }
                });

        UserDetailsServiceImpl service = new UserDetailsServiceImpl();
        setField(service, "userService", userService);
        setField(service, "permissionService", new SysPermissionService());

        UserDetails userDetails = service.loadUserByUsername("suyunhong");

        assertInstanceOf(LoginUser.class, userDetails);
        assertEquals("suyunhong", usernameRef.get());
        assertEquals(UserTypeEnums.ADMIN.getCode(), userTypeRef.get());
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
