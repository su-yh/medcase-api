package com.medcase.framework.web.service;

import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.enums.UserStatusEnums;
import com.medcase.common.utils.StringUtils;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import com.medcase.system.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 用户验证处理
 *
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private SysUserService userService;
    
    @Autowired
    private SysPermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SysUser user = userService.selectUserByUserName(username, UserTypeEnums.ADMIN.getCode());
        if (StringUtils.isNull(user)) {

            log.info("登录用户：{} 不存在.", username);
            throw ExceptionUtil.business(ErrorCodeEnums.ADMIN_LOGIN_FAILED);
        }
        else if (Boolean.TRUE.equals(user.getDelFlag())) {

            log.info("登录用户：{} 已被删除.", username);
            throw ExceptionUtil.business(ErrorCodeEnums.USER_DELETED);
        }
        else if (UserStatusEnums.DISABLE.getCode().equals(user.getStatus())) {

            log.info("登录用户：{} 已被停用.", username);
            throw ExceptionUtil.business(ErrorCodeEnums.USER_BLOCKED);
        }

        return createLoginUser(user);
    }

    public UserDetails createLoginUser(SysUser user) {

        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
    }
}
