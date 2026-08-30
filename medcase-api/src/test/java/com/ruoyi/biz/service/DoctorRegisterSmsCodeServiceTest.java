package com.ruoyi.biz.service;

import com.ruoyi.biz.sms.AliyunSmsService;
import com.ruoyi.biz.sms.AliyunSmsProperties;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.AbstractBusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoctorRegisterSmsCodeServiceTest {
    private static final String PHONE = "13800000000";

    @Mock
    private RedisCache redisCache;

    @Mock
    private AliyunSmsService aliyunSmsService;

    private AliyunSmsProperties smsProperties;

    private DoctorRegisterSmsCodeService smsCodeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        smsProperties = new AliyunSmsProperties();
        smsProperties.setEnabled(true);
        smsCodeService = new DoctorRegisterSmsCodeService(redisCache, aliyunSmsService, smsProperties);
    }

    @Test
    void sendCodeStoresSixDigitCodeAndCooldownAfterSmsIsSent() {
        when(redisCache.hasKey("doctor_register:sms_cooldown:" + PHONE)).thenReturn(false);

        smsCodeService.sendCode(PHONE);

        ArgumentCaptor<String> codeCaptor = ArgumentCaptor.forClass(String.class);
        verify(aliyunSmsService).sendVerificationCode(eq(PHONE), codeCaptor.capture());
        String code = codeCaptor.getValue();
        assertTrue(code.matches("\\d{6}"));
        verify(redisCache).setCacheObject(
                "doctor_register:sms_code:" + PHONE, code, 5, TimeUnit.MINUTES);
        verify(redisCache).setCacheObject(
                "doctor_register:sms_cooldown:" + PHONE, "1", 60, TimeUnit.SECONDS);
    }

    @Test
    void sendCodeUsesFixedCodeWithoutSendingSmsWhenSmsIsDisabled() {
        smsProperties.setEnabled(false);
        when(redisCache.hasKey("doctor_register:sms_cooldown:" + PHONE)).thenReturn(false);

        smsCodeService.sendCode(PHONE);

        verify(aliyunSmsService, never()).sendVerificationCode(
                eq(PHONE), org.mockito.ArgumentMatchers.anyString());
        verify(redisCache).setCacheObject(
                "doctor_register:sms_code:" + PHONE, "999999", 5, TimeUnit.MINUTES);
        verify(redisCache).setCacheObject(
                "doctor_register:sms_cooldown:" + PHONE, "1", 60, TimeUnit.SECONDS);
    }

    @Test
    void sendCodeRejectsPhoneInCooldown() {
        when(redisCache.hasKey("doctor_register:sms_cooldown:" + PHONE)).thenReturn(true);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class, () -> smsCodeService.sendCode(PHONE));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_SMS_CODE_SEND_TOO_FREQUENT, exception.getEc());
        verify(aliyunSmsService, never()).sendVerificationCode(
                eq(PHONE), org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void verifyCodeConsumesMatchingCode() {
        when(redisCache.getCacheObject("doctor_register:sms_code:" + PHONE)).thenReturn("123456");

        smsCodeService.verifyCode(PHONE, "123456");

        verify(redisCache).deleteObject("doctor_register:sms_code:" + PHONE);
    }

    @Test
    void verifyCodeAcceptsFixedCodeWhenSmsIsDisabled() {
        smsProperties.setEnabled(false);

        smsCodeService.verifyCode(PHONE, "999999");

        verify(redisCache, never()).getCacheObject("doctor_register:sms_code:" + PHONE);
        verify(redisCache, never()).deleteObject("doctor_register:sms_code:" + PHONE);
    }

    @Test
    void verifyCodeRejectsNonFixedCodeWhenSmsIsDisabled() {
        smsProperties.setEnabled(false);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class, () -> smsCodeService.verifyCode(PHONE, "123456"));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_SMS_CODE_INVALID, exception.getEc());
        verify(redisCache, never()).getCacheObject("doctor_register:sms_code:" + PHONE);
    }

    @Test
    void verifyCodeRejectsExpiredCode() {
        when(redisCache.getCacheObject("doctor_register:sms_code:" + PHONE)).thenReturn(null);

        AbstractBusinessException exception = assertThrows(
                AbstractBusinessException.class, () -> smsCodeService.verifyCode(PHONE, "123456"));

        assertEquals(ErrorCodeEnums.DOCTOR_REGISTER_SMS_CODE_EXPIRED, exception.getEc());
    }
}
