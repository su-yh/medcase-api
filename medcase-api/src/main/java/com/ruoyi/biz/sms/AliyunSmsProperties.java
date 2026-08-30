package com.ruoyi.biz.sms;

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

    private String signName;

    private String templateCode;

    public boolean isConfigured() {
        return StringUtils.hasText(accessKeyId)
                && StringUtils.hasText(accessKeySecret)
                && StringUtils.hasText(signName)
                && StringUtils.hasText(templateCode);
    }
}
