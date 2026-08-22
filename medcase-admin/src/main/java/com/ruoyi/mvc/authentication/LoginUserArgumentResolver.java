package com.ruoyi.mvc.authentication;

import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.mvc.authentication.annotation.CurrLoginUser;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 自定义参数解析器的实现，该实现针对在Controller 的handler 接口方法中的参数做匹配。
 * 匹配上的参数，则会为该参数绑定上一个值，然后在handler 方法中就可以直接得到该值使用了。
 */
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        CurrLoginUser ann = parameter.getParameterAnnotation(CurrLoginUser.class);
        if (ann == null) {
            return false;
        }

        Class<?> parameterType = parameter.getParameterType();
        return LoginUser.class.isAssignableFrom(parameterType);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer container,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        CurrLoginUser ann = parameter.getParameterAnnotation(CurrLoginUser.class);
        assert ann != null;

        LoginUser currUser = SecurityUtils.getLoginUser();
        if (currUser == null) {    // 用户未登录
            if (ann.required()) {    // 用户必须登录
                throw ExceptionUtil.business(ErrorCodeEnums.USER_NOT_LOGIN);
            }
        } else {
            UserTypeEnums userType = currUser.getUser().getUserType();
            if (userType != ann.userType()) {
                throw ExceptionUtil.business(ErrorCodeEnums.USER_TYPE_NOT_MATCH);
            }
        }

        return currUser;
    }
}


