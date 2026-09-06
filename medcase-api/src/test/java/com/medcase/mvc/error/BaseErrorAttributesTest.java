package com.medcase.mvc.error;

import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BaseErrorAttributesTest {

    @Test
    void businessErrorUsesTheSameResponseEnvelopeAsSuccess() {

        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage(
                ErrorCodeEnums.ADMIN_LOGIN_FAILED.getCode(),
                Locale.SIMPLIFIED_CHINESE,
                "用户不存在/密码错误");

        BaseErrorAttributes errorAttributes = new BaseErrorAttributes(messageSource);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(
                RequestDispatcher.ERROR_EXCEPTION,
                ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED));

        Map<String, Object> result = errorAttributes.getErrorAttributes(
                new ServletWebRequest(request),
                ErrorAttributeOptions.defaults());

        assertEquals("error.code.admin.login.failed", result.get("code"));
        assertEquals("用户不存在/密码错误", result.get("msg"));
        assertTrue(result.containsKey("data"));
        assertNull(result.get("data"));
        assertEquals(3, result.size());
    }
}
