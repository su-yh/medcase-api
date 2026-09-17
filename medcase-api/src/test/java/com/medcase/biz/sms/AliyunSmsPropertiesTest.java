package com.medcase.biz.sms;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class AliyunSmsPropertiesTest {
    @Test
    void rejectsMissingRequiredPropertiesWhenSmsIsEnabled() {
        new ApplicationContextRunner()
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues("sms.aliyun.enabled=true")
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void allowsMissingRequiredPropertiesWhenSmsIsDisabled() {
        new ApplicationContextRunner()
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues("sms.aliyun.enabled=false")
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Test
    void acceptsAllRequiredPropertiesWhenSmsIsEnabled() {
        new ApplicationContextRunner()
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues(
                        "sms.aliyun.enabled=true",
                        "sms.aliyun.endpoint=dysmsapi.aliyuncs.com",
                        "sms.aliyun.access-key-id=access-key-id",
                        "sms.aliyun.access-key-secret=access-key-secret",
                        "sms.aliyun.sign-name=sign-name",
                        "sms.aliyun.template-code=template-code")
                .run(context -> assertThat(context).hasNotFailed());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(AliyunSmsProperties.class)
    static class TestConfiguration {
    }
}
