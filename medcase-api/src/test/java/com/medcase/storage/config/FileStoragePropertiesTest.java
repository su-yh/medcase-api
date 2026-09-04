package com.medcase.storage.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

class FileStoragePropertiesTest {
    @Test
    void rejectsMissingRequiredMinioPropertiesDuringConfigurationBinding() {
        new ApplicationContextRunner()
                .withUserConfiguration(TestConfiguration.class)
                .withPropertyValues(
                        "attachment.storage.minio.access-key=access-key",
                        "attachment.storage.minio.secret-key=secret-key",
                        "attachment.storage.minio.bucket=attachment")
                .run(context -> assertThat(context).hasFailed());
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties(FileStorageProperties.class)
    static class TestConfiguration {
    }
}
