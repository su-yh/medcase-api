package com.medcase.storage.config;

import com.medcase.storage.service.FileStorageService;
import com.medcase.storage.service.impl.MinioFileStorageService;
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
}
