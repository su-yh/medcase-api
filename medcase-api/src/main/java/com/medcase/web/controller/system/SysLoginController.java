package com.medcase.web.controller.system;

import com.medcase.common.annotation.Anonymous;
import com.medcase.common.core.domain.entity.SysMenu;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginBody;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.core.text.Convert;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.common.utils.DateUtils;
import com.medcase.common.utils.SecurityUtils;
import com.medcase.framework.web.service.SysPermissionService;
import com.medcase.framework.web.service.TokenService;
import com.medcase.framework.web.service.UserLoginService;
import com.medcase.system.domain.vo.RouterVo;
import com.medcase.system.service.SysConfigService;
import com.medcase.system.service.SysMenuService;
import com.medcase.web.controller.system.dto.LoginResponse;
import com.medcase.web.controller.system.dto.LoginUserInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 登录验证
 * 
 */
@RestController
public class SysLoginController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysConfigService configService;

    /**
     * 登录方法
     * @param loginBody 登录信息
     * @return 结果
     */
    @Anonymous
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginBody loginBody) {

        // 生成令牌
        String token = userLoginService.login(
                loginBody.getUsername(),
                loginBody.getPassword(),
                loginBody.getCode(),
                loginBody.getUuid(),
                UserTypeEnums.ADMIN);
        return new LoginResponse(token);
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public LoginUserInfoResponse getInfo() {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions)) {

            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        LoginUserInfoResponse data = new LoginUserInfoResponse(
                user,
                roles,
                permissions,
                getSysAccountChrtype(),
                initPasswordIsModify(user.getPwdUpdateDate()),
                passwordIsExpiration(user.getPwdUpdateDate()));
        return data;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public List<RouterVo> getRouters() {

        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return menuService.buildMenus(menus);
    }

    // 获取用户密码自定义配置规则
    public String getSysAccountChrtype() {

        return Convert.toStr(configService.selectConfigByKey("sys.account.chrtype"), "0");
    }

    // 检查初始密码是否提醒修改
    public boolean initPasswordIsModify(Date pwdUpdateDate) {

        Integer initPasswordModify = Convert.toInt(configService.selectConfigByKey("sys.account.initPasswordModify"));
        return initPasswordModify != null && initPasswordModify == 1 && pwdUpdateDate == null;
    }

    // 检查密码是否过期
    public boolean passwordIsExpiration(Date pwdUpdateDate) {

        Integer passwordValidateDays = Convert.toInt(configService.selectConfigByKey("sys.account.passwordValidateDays"));
        if (passwordValidateDays != null && passwordValidateDays > 0) {

            if (pwdUpdateDate == null) {

                // 如果从未修改过初始密码，直接提醒过期
                return true;
            }
            Date nowDate = DateUtils.getNowDate();
            return DateUtils.differentDaysByMillisecond(nowDate, pwdUpdateDate) > passwordValidateDays;
        }
        return false;
    }
}
