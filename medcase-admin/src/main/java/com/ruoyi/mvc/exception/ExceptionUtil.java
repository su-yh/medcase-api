package com.ruoyi.mvc.exception;

import com.ruoyi.mvc.error.IErrorCode;

public class ExceptionUtil {

    public static AbstractBusinessException business(IErrorCode ec, Object... params) {
        return new BaseException(ExceptionCategory.BUSINESS, ec, params);
    }

    public static AbstractBusinessException noRollBusiness(IErrorCode ec, Object... params) {
        return new NoRollbackException(ExceptionCategory.BUSINESS, ec, params);
    }

    /**
     * 对于当前系统来说，所有的异常都返回200，所以这里的方法没有意义了。
     */
    @Deprecated
    public static BaseException system(IErrorCode ec, Object... params) {
        return new BaseException(ExceptionCategory.SYSTEM, ec, params);
    }
}
