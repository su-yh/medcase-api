package com.medcase.framework.interceptor;

import java.lang.reflect.Method;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import com.medcase.common.annotation.RepeatSubmit;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;

/**
 * 防止重复提交拦截器
 *
 */
@Component
public abstract class RepeatSubmitInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);
            if (annotation != null) {

                if (this.isRepeatSubmit(request, annotation)) {
                    throw ExceptionUtil.business(ErrorCodeEnums.REPEAT_SUBMIT, annotation.message());
                }
            }
            return true;
        }
        else {

            return true;
        }
    }

    /**
     * 验证是否重复提交由子类实现具体的防重复提交的规则
     *
     * @param request 请求信息
     * @param annotation 防重复注解参数
     * @return 结果
     * @throws Exception
     */
    public abstract boolean isRepeatSubmit(HttpServletRequest request, RepeatSubmit annotation);
}
