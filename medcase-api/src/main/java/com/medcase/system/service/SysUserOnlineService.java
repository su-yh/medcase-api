package com.medcase.system.service;

import org.springframework.stereotype.Service;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.system.domain.SysUserOnline;

/**
 * 在线用户 服务层处理
 * 
 */
@Service
public class SysUserOnlineService {

    /**
     * 通过登录地址查询信息
     * 
     * @param ipaddr 登录地址
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByIpaddr(String ipaddr, LoginUser user) {

        if (java.util.Objects.equals(ipaddr, user.getIpaddr())) {

            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过用户名称查询信息
     * 
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByUserName(String userName, LoginUser user) {

        if (java.util.Objects.equals(userName, user.getUsername())) {

            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 通过登录地址/用户名称查询信息
     * 
     * @param ipaddr 登录地址
     * @param userName 用户名称
     * @param user 用户信息
     * @return 在线用户信息
     */
    public SysUserOnline selectOnlineByInfo(String ipaddr, String userName, LoginUser user) {

        if (java.util.Objects.equals(ipaddr, user.getIpaddr())
                && java.util.Objects.equals(userName, user.getUsername())) {

            return loginUserToUserOnline(user);
        }
        return null;
    }

    /**
     * 设置在线用户信息
     * 
     * @param user 用户信息
     * @return 在线用户
     */
    public SysUserOnline loginUserToUserOnline(LoginUser user) {

        if (user == null || user.getUser() == null) {

            return null;
        }
        SysUserOnline sysUserOnline = new SysUserOnline();
        sysUserOnline.setTokenId(user.getToken());
        sysUserOnline.setUserName(user.getUsername());
        sysUserOnline.setIpaddr(user.getIpaddr());
        sysUserOnline.setLoginLocation(user.getLoginLocation());
        sysUserOnline.setBrowser(user.getBrowser());
        sysUserOnline.setOs(user.getOs());
        sysUserOnline.setLoginTime(user.getLoginTime());
        if (user.getUser().getDept() != null) {

            sysUserOnline.setDeptName(user.getUser().getDept().getDeptName());
        }
        return sysUserOnline;
    }
}
