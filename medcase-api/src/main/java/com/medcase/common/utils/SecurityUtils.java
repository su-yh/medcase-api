package com.medcase.common.utils;

import com.medcase.common.constant.Constants;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * 安全服务工具类
 * 
 */
public class SecurityUtils {


    /**
     * 用户ID
     **/
    public static Long getUserId() {

        try {

            return getLoginUser().getUserId();
        }
        catch (Exception e) {

            throw ExceptionUtil.business(ErrorCodeEnums.USER_ID_RESOLVE_FAILED);
        }
    }

    /**
     * 获取部门ID
     **/
    public static Long getDeptId() {

        try {

            return getLoginUser().getDeptId();
        }
        catch (Exception e) {

            throw ExceptionUtil.business(ErrorCodeEnums.DEPT_ID_RESOLVE_FAILED);
        }
    }

    /**
     * 获取用户账户
     **/
    public static String getUsername() {

        try {

            return getLoginUser().getUsername();
        }
        catch (Exception e) {

            throw ExceptionUtil.business(ErrorCodeEnums.USERNAME_RESOLVE_FAILED);
        }
    }

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
     * @return 结果
     */
    public static boolean isAdmin() {

        return isAdmin(getUserId());
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

    /**
     * 验证用户是否具备某权限
     * 
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermi(String permission) {

        return hasPermi(getLoginUser().getPermissions(), permission);
    }

    /**
     * 判断是否包含权限
     * 
     * @param authorities 权限列表
     * @param permission 权限字符串
     * @return 用户是否具备某权限
     */
    public static boolean hasPermi(Collection<String> authorities, String permission) {

        return authorities.stream().filter(StringUtils::hasText)
                .anyMatch(x -> Constants.ALL_PERMISSION.equals(x) || PatternMatchUtils.simpleMatch(x, permission));
    }

}
