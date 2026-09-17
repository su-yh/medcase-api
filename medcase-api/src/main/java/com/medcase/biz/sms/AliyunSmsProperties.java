package com.medcase.biz.sms;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.group.GroupSequenceProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * 阿里云短信配置。
 *
 * @author suyh
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "sms.aliyun")
@GroupSequenceProvider(AliyunSmsPropertiesGroupSequenceProvider.class)
public class AliyunSmsProperties {
    /**
     * 是否启用真实的阿里云短信发送。
     */
    private boolean enabled = true;

    @NotBlank(groups = AliyunSmsEnabled.class)
    private String endpoint;

    @NotBlank(groups = AliyunSmsEnabled.class)
    private String accessKeyId;

    @NotBlank(groups = AliyunSmsEnabled.class)
    private String accessKeySecret;

    /**
     * 【签名管理】里面可以看到签名名称
     */
    @NotBlank(groups = AliyunSmsEnabled.class)
    private String signName;

    /**
     * 【模板管理】点击模板名称可以看到模板详情，里面会有模板CODE
     */
    @NotBlank(groups = AliyunSmsEnabled.class)
    private String templateCode;

    public boolean isConfigured() {
        return StringUtils.hasText(endpoint)
                && StringUtils.hasText(accessKeyId)
                && StringUtils.hasText(accessKeySecret)
                && StringUtils.hasText(signName)
                && StringUtils.hasText(templateCode);
    }
}
