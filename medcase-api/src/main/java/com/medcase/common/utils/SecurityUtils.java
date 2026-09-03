package com.medcase.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.PatternMatchUtils;
import com.medcase.common.constant.Constants;
import com.medcase.common.core.domain.entity.SysRole;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;

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

    /**
     * 验证用户是否拥有某个角色
     * 
     * @param role 角色标识
     * @return 用户是否具备某角色
     */
    public static boolean hasRole(String role) {

        List<SysRole> roleList = getLoginUser().getUser().getRoles();
        Collection<String> roles = roleList.stream().map(SysRole::getRoleKey).collect(Collectors.toSet());
        return hasRole(roles, role);
    }

    /**
     * 判断是否包含角色
     * 
     * @param roles 角色列表
     * @param role 角色
     * @return 用户是否具备某角色权限
     */
    public static boolean hasRole(Collection<String> roles, String role) {

        return roles.stream().filter(StringUtils::hasText)
                .anyMatch(x -> Constants.SUPER_ADMIN.equals(x) || PatternMatchUtils.simpleMatch(x, role));
    }

}
