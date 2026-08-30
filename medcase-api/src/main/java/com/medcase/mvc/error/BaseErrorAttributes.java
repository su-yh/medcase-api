package com.medcase.mvc.error;

import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.mvc.exception.ExceptionCategory;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/**
 * 派生自spring mvc 默认异常处理类
 * 添加一些自定义的响应属性值。
 * <p>
 * 参考：{@link ErrorMvcAutoConfiguration#errorAttributes()} 方法，创建的一个bean 对象
 * 这里的方法也是规划该方法的bean 实现，并在生成响应体里扩展自己的一些属性。
 * 但是如果是自定义的异常最好还是添加一个ControllerAdvice 的全局性的单独处理。
 * <p>
 * <p>
 * 4xx 5xx 都是错误，其他都不被识别为错误。
 * 但是200 的需要判断，最终都是通过 success 的boolean 结果判断成功与失败。
 * 以前的code 现在已经不用了
 * 现在的status 与HttpStatus 一致
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class BaseErrorAttributes extends DefaultErrorAttributes {
    // i18n 国际化
    private final MessageSource messageSource;

    @Override
    public @NonNull Map<String, @Nullable Object> getErrorAttributes(
            @NonNull WebRequest webRequest, @NonNull ErrorAttributeOptions options) {

        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
        String timestampFormat = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

        Map<String, @Nullable Object> errorAttributes = super.getErrorAttributes(webRequest, options);
        errorAttributes.remove("error");
        errorAttributes.put("timestamp_zh", timestampFormat);

        Throwable throwable = getError(webRequest);

        if (throwable != null) {
            IErrorCode ec = ErrorCodeEnums.SERVICE_ERROR;
            Object[] params = null;
            if (AbstractBusinessException.class.isAssignableFrom(throwable.getClass())) {
                AbstractBusinessException exception = (AbstractBusinessException) throwable;

                if (ExceptionCategory.SYSTEM.equals(exception.getCategory())) {
                    // 系统异常，打印堆栈信息。
                    log.warn("system exception, timestamp: {}", timestampFormat, throwable);
                }
                if (exception.getEc() == ErrorCodeEnums.SERVICE_ERROR) {
                    // 当前环境没有使用系统异常和业务异常，这里就判断对应的ID 做处理打印堆栈信息。
                    log.warn("service exception, timestamp: {}", timestampFormat, throwable);
                }

                ec = exception.getEc();
                params = exception.getParams();
            } else if (AccessDeniedException.class.isAssignableFrom(throwable.getClass())) {
                ec = ErrorCodeEnums.ACCESS_DENIED;
            }

            Locale locale = webRequest.getLocale();
            String message = messageSource.getMessage(ec.getCode(), params, ec.getMsg(), locale);
            errorAttributes.put("code", ec.getCode());
            errorAttributes.put("msg", message);
        }

        return errorAttributes;
    }
}
