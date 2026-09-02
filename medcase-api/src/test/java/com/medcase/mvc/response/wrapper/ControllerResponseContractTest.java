package com.medcase.mvc.response.wrapper;

import com.medcase.biz.controller.UserAuthPortalController;
import com.medcase.biz.controller.CasePortalController;
import com.medcase.biz.controller.DoctorCaseReviewAdminController;
import com.medcase.biz.controller.UserProfilePortalController;
import com.medcase.biz.controller.DoctorUserAdminController;
import com.medcase.mvc.response.R;
import com.medcase.mp.mybatis.PageResult;
import com.medcase.mp.mybatis.PageParam;
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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ControllerResponseContractTest {
    private static final List<Class<?>> CONTROLLERS = List.of(
            UserAuthPortalController.class,
            CasePortalController.class,
            DoctorCaseReviewAdminController.class,
            UserProfilePortalController.class,
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

    @Test
    void systemManagementControllersShouldUseDedicatedDtos() {
        assertDtoContract(
                SysConfigController.class,
                "list",
                "com.medcase.web.controller.system.dto.ConfigQueryRequest",
                "com.medcase.web.controller.system.dto.ConfigResponse");
        assertDtoContract(
                SysNoticeController.class,
                "list",
                "com.medcase.web.controller.system.dto.NoticeQueryRequest",
                "com.medcase.web.controller.system.dto.NoticeResponse");
        assertDtoContract(
                SysPostController.class,
                "list",
                "com.medcase.web.controller.system.dto.PostQueryRequest",
                "com.medcase.web.controller.system.dto.PostResponse");
    }

    private void assertDtoContract(
            Class<?> controllerType, String methodName,
            String requestTypeName, String responseTypeName) {
        Method method = findMethod(controllerType, methodName);

        assertEquals(PageParam.class, method.getParameterTypes()[0]);
        assertEquals(requestTypeName, method.getParameterTypes()[1].getName());
        assertEquals(PageResult.class, method.getReturnType());

        ParameterizedType pageResultType = (ParameterizedType) method.getGenericReturnType();
        assertEquals(responseTypeName,
                pageResultType.getActualTypeArguments()[0].getTypeName());
    }

    private Method findMethod(Class<?> controllerType, String methodName) {
        for (Method method : controllerType.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new AssertionError(controllerType.getName() + "#" + methodName + " does not exist");
    }
}
