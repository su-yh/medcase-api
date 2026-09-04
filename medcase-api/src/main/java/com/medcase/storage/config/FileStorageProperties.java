package com.medcase.storage.config;

import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 文件存储配置。
 *
 * @author suyh
 */
@Data
@Validated
@ConfigurationProperties(prefix = "attachment.storage")
public class FileStorageProperties {
    @Valid
    private Minio minio = new Minio();

    @Data
    public static class Minio {
        /**
         * MinIO 服务地址，例如 https://minio.example.com。
         */
        @NotBlank
        private String endpoint;

        /**
         * MinIO Access Key。
         */
        @NotBlank
        private String accessKey;

        /**
         * MinIO Secret Key。
         */
        @NotBlank
        private String secretKey;

        /**
         * 对象存储桶名称。
         */
        @NotBlank
        private String bucket;

        /**
         * 对象前缀，可为空。
         */
        private String objectPrefix = "";
    }
}
