package com.ruoyi.mvc.exception;

import com.ruoyi.mvc.error.IErrorCode;
import lombok.Getter;
import org.jspecify.annotations.NonNull;

@Getter
public class BaseException extends AbstractBusinessException {

    public BaseException(@NonNull ExceptionCategory category, IErrorCode ec, Object... params) {
        super(category, ec, params);
    }
}
