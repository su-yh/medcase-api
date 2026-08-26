package com.ruoyi.mvc.exception;

import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionUtilTest {
    @Test
    void createsBusinessExceptionWithMessage() {
        AbstractBusinessException exception = ExceptionUtil.business(ErrorCodeEnums.SUPER_ADMIN_USER_OPERATION);

        assertEquals(ErrorCodeEnums.SUPER_ADMIN_USER_OPERATION, exception.getEc());
    }

    @Test
    void createsBusinessExceptionWithParameters() {
        AbstractBusinessException exception = ExceptionUtil.business(
                ErrorCodeEnums.CONFIG_BUILTIN_DELETE, "sys.demo");

        assertEquals(ErrorCodeEnums.CONFIG_BUILTIN_DELETE, exception.getEc());
        assertEquals("sys.demo", exception.getParams()[0]);
    }
}
