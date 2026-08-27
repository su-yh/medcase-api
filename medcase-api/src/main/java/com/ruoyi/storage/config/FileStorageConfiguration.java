package com.ruoyi.storage.config;

import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import com.ruoyi.storage.service.FileStorageService;
import com.ruoyi.storage.service.impl.MinioFileStorageService;
import io.minio.MinioClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件存储实现配置。
 *
 * @author suyh
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FileStorageProperties.class)
public class FileStorageConfiguration {
    @Bean
    public MinioClient minioClient(FileStorageProperties properties) {
        FileStorageProperties.Minio minio = properties.getMinio();
        requireConfigured(minio.getEndpoint(), "attachment.storage.minio.endpoint");
        requireConfigured(minio.getAccessKey(), "attachment.storage.minio.access-key");
        requireConfigured(minio.getSecretKey(), "attachment.storage.minio.secret-key");
        requireConfigured(minio.getBucket(), "attachment.storage.minio.bucket");

        return MinioClient.builder()
                .endpoint(minio.getEndpoint())
                .credentials(minio.getAccessKey(), minio.getSecretKey())
                .build();
    }

    @Bean
    public FileStorageService minioFileStorageService(
            FileStorageProperties properties, MinioClient minioClient) {
        return new MinioFileStorageService(properties, minioClient);
    }

    private void requireConfigured(String value, String propertyName) {
        if (value == null || value.isBlank()) {
            throw ExceptionUtil.business(ErrorCodeEnums.ATTACHMENT_CONFIG_MISSING, propertyName);
        }
    }
}
