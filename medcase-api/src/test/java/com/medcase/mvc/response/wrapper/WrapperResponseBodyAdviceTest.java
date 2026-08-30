package com.medcase.mvc.response.wrapper;

import com.medcase.mvc.response.R;
import com.medcase.mvc.response.annotation.WrapperResponseAdvice;
import com.medcase.web.controller.system.SysVersionController;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WrapperResponseBodyAdviceTest {

    @Test
    void medcaseControllersShouldBeIncludedInAutomaticWrapping() {
        assertTrue(WrapperResponseBodyAdvice.supportsWrapper(
                SysVersionController.class, String.class, null));
    }

    @Test
    void explicitRResponseShouldNotBeWrappedAgain() {
        assertFalse(WrapperResponseBodyAdvice.supportsWrapper(
                SysVersionController.class, R.class, null));
    }

    @Test
    void disabledMethodShouldSkipAutomaticWrapping() throws NoSuchMethodException {
        Method method = TestController.class.getDeclaredMethod("rawResponse");
        WrapperResponseAdvice annotation = method.getAnnotation(WrapperResponseAdvice.class);

        assertFalse(WrapperResponseBodyAdvice.supportsWrapper(
                TestController.class, String.class, annotation));
    }

    private static class TestController {
        @WrapperResponseAdvice(enable = false)
        public String rawResponse() {
            return "raw";
        }
    }
}
