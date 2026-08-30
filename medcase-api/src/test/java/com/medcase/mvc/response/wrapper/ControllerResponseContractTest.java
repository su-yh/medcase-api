package com.medcase.mvc.response.wrapper;

import com.medcase.biz.controller.DoctorAuthPortalController;
import com.medcase.biz.controller.DoctorCasePortalController;
import com.medcase.biz.controller.DoctorCaseReviewAdminController;
import com.medcase.biz.controller.DoctorProfilePortalController;
import com.medcase.biz.controller.DoctorUserAdminController;
import com.medcase.mvc.response.R;
import com.medcase.web.controller.common.CaptchaController;
import com.medcase.web.controller.file.FileStorageController;
import com.medcase.web.controller.monitor.CacheController;
import com.medcase.web.controller.monitor.ServerController;
import com.medcase.web.controller.monitor.SysLogininforController;
import com.medcase.web.controller.monitor.SysOperlogController;
import com.medcase.web.controller.monitor.SysUserOnlineController;
import com.medcase.web.controller.system.SysConfigController;
import com.medcase.web.controller.system.SysDeptController;
import com.medcase.web.controller.system.SysDictDataController;
import com.medcase.web.controller.system.SysDictTypeController;
import com.medcase.web.controller.system.SysIndexController;
import com.medcase.web.controller.system.SysLoginController;
import com.medcase.web.controller.system.SysMenuController;
import com.medcase.web.controller.system.SysNoticeController;
import com.medcase.web.controller.system.SysPostController;
import com.medcase.web.controller.system.SysProfileController;
import com.medcase.web.controller.system.SysRegisterController;
import com.medcase.web.controller.system.SysRoleController;
import com.medcase.web.controller.system.SysUserController;
import com.medcase.web.controller.system.SysVersionController;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ControllerResponseContractTest {
    private static final List<Class<?>> CONTROLLERS = List.of(
            DoctorAuthPortalController.class,
            DoctorCasePortalController.class,
            DoctorCaseReviewAdminController.class,
            DoctorProfilePortalController.class,
            DoctorUserAdminController.class,
            CaptchaController.class,
            FileStorageController.class,
            CacheController.class,
            ServerController.class,
            SysLogininforController.class,
            SysOperlogController.class,
            SysUserOnlineController.class,
            SysConfigController.class,
            SysDeptController.class,
            SysDictDataController.class,
            SysDictTypeController.class,
            SysIndexController.class,
            SysLoginController.class,
            SysMenuController.class,
            SysNoticeController.class,
            SysPostController.class,
            SysProfileController.class,
            SysRegisterController.class,
            SysRoleController.class,
            SysUserController.class,
            SysVersionController.class);

    @Test
    void controllersShouldLeaveResponseWrappingToAdvice() {
        for (Class<?> controller : CONTROLLERS) {
            for (var method : controller.getDeclaredMethods()) {
                assertFalse(R.class.isAssignableFrom(method.getReturnType()),
                        () -> controller.getName() + "#" + method.getName()
                                + " must return its business value instead of R");
            }
        }
    }
}
