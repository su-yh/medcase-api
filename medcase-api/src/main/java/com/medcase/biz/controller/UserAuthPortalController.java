package com.medcase.biz.controller;

import com.medcase.common.annotation.Anonymous;
import com.medcase.biz.request.UserLoginRequest;
import com.medcase.biz.request.UserRegisterRequest;
import com.medcase.biz.request.UserRegisterSmsCodeRequest;
import com.medcase.biz.service.UserAuthService;
import com.medcase.biz.service.UserRegisterSmsCodeService;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 病例端用户认证接口
 *
 * @author suyh
 * @since 2026-08-19
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/biz/user-auth")
public class UserAuthPortalController {
    private final UserAuthService userAuthService;

    private final UserRegisterSmsCodeService smsCodeService;

    @Anonymous
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody UserLoginRequest loginBody) {
        log.trace("user auth controller login");
        return userAuthService.login(loginBody);
    }

    @Anonymous
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@RequestBody @Valid UserRegisterRequest registerBody) {
        log.trace("user auth controller register");
        userAuthService.register(registerBody);
    }

    @Anonymous
    @RequestMapping(value = "/register/sms-code", method = RequestMethod.POST)
    public void sendRegisterSmsCode(@RequestBody UserRegisterSmsCodeRequest request) {
        smsCodeService.sendCode(request.getPhone());
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout(@CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user) {
        log.trace("user auth controller logout");
        userAuthService.logout(user);
    }

    @DeleteMapping("/account")
    public void deleteAccount(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user) {
        log.trace("user auth controller delete account, userId={}", user.getUserId());
        userAuthService.deleteAccount(user);
    }
}
