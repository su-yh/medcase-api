package com.medcase.biz.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 阿里云短信配置。
 *
 * @author suyh
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms.aliyun")
public class AliyunSmsProperties {
    /**
     * 是否启用真实的阿里云短信发送。
     */
    private boolean enabled = true;

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    /**
     * 【签名管理】里面可以看到签名名称
     */
    private String signName;

    /**
     * 【模板管理】点击模板名称可以看到模板详情，里面会有模板CODE
     */
    private String templateCode;

    public boolean isConfigured() {
        return StringUtils.hasText(accessKeyId)
                && StringUtils.hasText(accessKeySecret)
                && StringUtils.hasText(signName)
                && StringUtils.hasText(templateCode);
    }
}
