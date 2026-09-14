package com.medcase.common.utils;

import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全服务工具类
 * 
 */
public class SecurityUtils {

    /**
     * 获取用户
     **/
    public static LoginUser getLoginUser() {

        try {

            return (LoginUser) getAuthentication().getPrincipal();
        }
        catch (Exception e) {

            throw ExceptionUtil.business(ErrorCodeEnums.LOGIN_USER_RESOLVE_FAILED);
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {

        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 是否为管理员
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {

        return userId != null && 1L == userId;
    }
}
