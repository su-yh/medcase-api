package com.ruoyi.mvc.error;

import com.base.web.exception.AbstractBusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class BaseHandlerExceptionResolver extends DefaultHandlerExceptionResolver {
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1000;
    }

    @Override
    public ModelAndView doResolveException(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            Object handler,
            @NonNull Exception ex) {
        // 如果spring mvc 能够识别的异常，就先闪给spring mvc 来处理，识别不了并没有处理的异常，这里将会返回null。
        ModelAndView mav = super.doResolveException(request, response, handler, ex);
        if (mav != null) {
            return mav;
        }

        try {
            // spring mvc 不识别且没处理的异常，我们自己处理。
            if (ex instanceof AbstractBusinessException) {
                return handleOverseasProxyException((AbstractBusinessException) ex, request, response, handler);
            }

            if (AccessDeniedException.class.isAssignableFrom(ex.getClass())) {
                log.info("access denied.");
            } else {
                log.warn("exception.", ex);
            }

            // 剩下的所有异常处理，都由它来解决。这些剩下的异常我们自己也不识别，所以放在这里处理。
            return handlerException(ex, request, response, handler);
        } catch (Exception handlerEx) {
            log.warn("Failure while trying to resolve exception [{}]", ex.getClass().getName(), handlerEx);
        }

        return null;
    }

    protected ModelAndView handleOverseasProxyException(
            @NonNull AbstractBusinessException ex, @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(HttpServletResponse.SC_OK, ex.getMessage());
        return new ModelAndView();
    }

    protected ModelAndView handlerException(
            @NonNull Exception ex, @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @Nullable Object handler) throws IOException {
        response.sendError(HttpServletResponse.SC_OK, ex.getMessage());
        return new ModelAndView();
    }

    @NonNull
    @Override
    protected String buildLogMessage(@NonNull Exception ex, @NonNull HttpServletRequest request) {
        if (ex instanceof AbstractBusinessException) {
            AbstractBusinessException businessException = (AbstractBusinessException) ex;
            IErrorCode ec = businessException.getEc();
            return String.format("Resolved [error code: %d, msg: %s]", ec.getCode(), ec.getMsg());
        }

        return super.buildLogMessage(ex, request);
    }
}
