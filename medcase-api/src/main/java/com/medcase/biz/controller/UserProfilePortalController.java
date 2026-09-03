package com.medcase.biz.controller;

import com.medcase.biz.request.UserProfilePasswordRequest;
import com.medcase.biz.request.UserProfilePhoneRequest;
import com.medcase.biz.request.UserProfileSubmitRequest;
import com.medcase.biz.response.UserProfileVO;
import com.medcase.biz.service.UserProfileService;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.mvc.authentication.annotation.CurrLoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 病例端用户资料接口
 *
 * @author suyh
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/biz/user-profile")
public class UserProfilePortalController {
    private final UserProfileService userProfileService;

    @RequestMapping(method = RequestMethod.GET)
    public UserProfileVO me(@CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user) {
        return userProfileService.me(user);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void submit(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user,
            @RequestBody @Valid UserProfileSubmitRequest request) {
        userProfileService.submit(user, request);
    }

    @PutMapping("/phone")
    public void updatePhone(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user,
            @RequestBody @Valid UserProfilePhoneRequest request) {
        userProfileService.updatePhone(user, request);
    }

    @PutMapping("/password")
    public void updatePassword(
            @CurrLoginUser(userType = {UserTypeEnums.DOCTOR, UserTypeEnums.PATIENT}) LoginUser user,
            @RequestBody @Valid UserProfilePasswordRequest request) {
        userProfileService.updatePassword(user, request);
    }
}
