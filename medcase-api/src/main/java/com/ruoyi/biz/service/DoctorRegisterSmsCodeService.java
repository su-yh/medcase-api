package com.ruoyi.biz.service;

import com.ruoyi.biz.sms.AliyunSmsProperties;
import com.ruoyi.biz.sms.AliyunSmsService;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 医生注册短信验证码服务。
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
public class DoctorRegisterSmsCodeService {
    private static final Pattern MOBILE_PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CODE_EXPIRATION_MINUTES = 5;
    private static final int SEND_COOLDOWN_SECONDS = 60;
    private static final String FIXED_SMS_CODE = "999999";

    private final RedisCache redisCache;

    private final AliyunSmsService aliyunSmsService;

    private final AliyunSmsProperties smsProperties;

    public void sendCode(String phone) {
        validatePhone(phone);
        if (Boolean.TRUE.equals(redisCache.hasKey(getCooldownKey(phone)))) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_SMS_CODE_SEND_TOO_FREQUENT);
        }

        String code = smsProperties.isEnabled()
                ? String.format(Locale.ROOT, "%06d", RANDOM.nextInt(1_000_000))
                : FIXED_SMS_CODE;
        if (smsProperties.isEnabled()) {
            aliyunSmsService.sendVerificationCode(phone, code);
        }
        redisCache.setCacheObject(
                getCodeKey(phone), code, CODE_EXPIRATION_MINUTES, TimeUnit.MINUTES);
        redisCache.setCacheObject(
                getCooldownKey(phone), "1", SEND_COOLDOWN_SECONDS, TimeUnit.SECONDS);
    }

    public void verifyCode(String phone, String code) {
        validatePhone(phone);
        if (!StringUtils.hasText(code)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_SMS_CODE_EMPTY);
        }

        if (!smsProperties.isEnabled()) {
            if (!FIXED_SMS_CODE.equals(code)) {
                throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_SMS_CODE_INVALID);
            }
            return;
        }

        String cacheKey = getCodeKey(phone);
        String expectedCode = redisCache.getCacheObject(cacheKey);
        if (expectedCode == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_SMS_CODE_EXPIRED);
        }

        redisCache.deleteObject(cacheKey);
        if (!expectedCode.equals(code)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_SMS_CODE_INVALID);
        }
    }

    private void validatePhone(String phone) {
        if (!StringUtils.hasText(phone)) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_PHONE_EMPTY);
        }
        if (!MOBILE_PHONE_PATTERN.matcher(phone).matches()) {
            throw ExceptionUtil.business(ErrorCodeEnums.DOCTOR_REGISTER_PHONE_INVALID);
        }
    }

    private String getCodeKey(String phone) {
        return CacheConstants.DOCTOR_REGISTER_SMS_CODE_KEY + phone;
    }

    private String getCooldownKey(String phone) {
        return CacheConstants.DOCTOR_REGISTER_SMS_COOLDOWN_KEY + phone;
    }
}
