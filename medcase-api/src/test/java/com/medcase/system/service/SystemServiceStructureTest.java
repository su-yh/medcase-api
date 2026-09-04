package com.medcase.system.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SystemServiceStructureTest {
    @Test
    void systemServicesUseConcreteServiceClasses() {
        String[] serviceNames = {
                "SysConfigService",
                "SysDeptService",
                "SysDictDataService",
                "SysDictTypeService",
                "SysLogininforService",
                "SysMenuService",
                "SysNoticeReadService",
                "SysNoticeService",
                "SysOperLogService",
                "SysPostService",
                "SysRoleService",
                "SysUserOnlineService",
                "SysUserService"
        };

        assertAll(() -> {
            for (String serviceName : serviceNames) {
                assertThat(loadService(serviceName)).as(serviceName).isNotNull();
            }
        });
    }

    private Class<?> loadService(String serviceName) {
        try {
            return Class.forName("com.medcase.system.service." + serviceName);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
    }
}
