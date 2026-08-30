package com.medcase.mvc.exception;

import com.medcase.mvc.error.IErrorCode;
import org.jspecify.annotations.NonNull;

/**
 * @author suyh
 * @since 2024-10-21
 */
public class NoRollbackException extends AbstractBusinessException {
    public NoRollbackException(@NonNull ExceptionCategory category, IErrorCode ec, Object... params) {
        super(category, ec, params);
    }
}
