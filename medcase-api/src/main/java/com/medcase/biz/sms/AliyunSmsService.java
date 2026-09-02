package com.medcase.biz.sms;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.AbstractBusinessException;
import com.medcase.mvc.exception.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 阿里云短信发送服务。
 *
 * @author suyh
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AliyunSmsService {
    private final AliyunSmsProperties smsProperties;

    private volatile Client client;

    public void sendVerificationCode(String phone, String code) {
        if (!smsProperties.isConfigured()) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_REGISTER_SMS_CONFIG_MISSING);
        }

        SendSmsRequest request = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(smsProperties.getSignName())
                .setTemplateCode(smsProperties.getTemplateCode())
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            SendSmsResponse response = getClient().sendSms(request);
            String responseCode = response.getBody() == null ? null : response.getBody().getCode();
            if (!"OK".equals(responseCode)) {
                log.error("aliyun sms send failed, phone={}, requestId={}, code={}, message={}",
                        phone,
                        response.getBody() == null ? null : response.getBody().getRequestId(),
                        responseCode,
                        response.getBody() == null ? null : response.getBody().getMessage());
                throw ExceptionUtil.business(ErrorCodeEnums.USER_REGISTER_SMS_SEND_FAILED);
            }
        } catch (AbstractBusinessException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("aliyun sms send failed, phone={}", phone, exception);
            throw ExceptionUtil.business(ErrorCodeEnums.USER_REGISTER_SMS_SEND_FAILED);
        }
    }

    private Client getClient() throws Exception {
        Client currentClient = client;
        if (currentClient != null) {
            return currentClient;
        }

        synchronized (this) {
            if (client == null) {
                Config config = new Config()
                        .setAccessKeyId(smsProperties.getAccessKeyId())
                        .setAccessKeySecret(smsProperties.getAccessKeySecret())
                        .setEndpoint(smsProperties.getEndpoint());
                client = new Client(config);
            }
            return client;
        }
    }
}
