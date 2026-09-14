package com.medcase.web.controller.system;

import com.medcase.common.annotation.Anonymous;
import com.medcase.common.core.domain.model.LoginBody;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.framework.web.service.SysPermissionService;
import com.medcase.framework.web.service.TokenService;
import com.medcase.framework.web.service.UserLoginService;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import com.medcase.system.entity.SysMenuEntity;
import com.medcase.system.entity.SysUserEntity;
import com.medcase.system.service.SysMenuService;
import com.medcase.web.controller.system.dto.LoginResponse;
import com.medcase.web.controller.system.dto.LoginUserInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
     * 登出方法
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN)")
    @PostMapping("/logout")
    public void logout(@CurrLoginUser LoginUser loginUser) {
        tokenService.delLoginUser(loginUser.getToken());
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN)")
    @GetMapping("getInfo")
    public LoginUserInfoResponse getInfo(
            @CurrLoginUser LoginUser loginUser) {

        SysUserEntity user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions)) {

            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        return new LoginUserInfoResponse(user, roles, permissions);
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @PreAuthorize("@dp.hasAnyUserType(#loginUser, T(com.medcase.common.enums.UserTypeEnums).ADMIN)")
    @GetMapping("getRouters")
    public List<SysMenuEntity> getRouters(
            @CurrLoginUser LoginUser loginUser) {
        return menuService.selectMenuTreeByUserId(loginUser.getUserId());
    }

}
