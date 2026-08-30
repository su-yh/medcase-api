package com.medcase.storage.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件存储配置。
 *
 * @author suyh
 */
@Data
@ConfigurationProperties(prefix = "attachment.storage")
public class FileStorageProperties {
    private Minio minio = new Minio();

    @Data
    public static class Minio {
        /**
         * MinIO 服务地址，例如 https://minio.example.com。
         */
        private String endpoint;

        /**
         * MinIO Access Key。
         */
        private String accessKey;

        /**
         * MinIO Secret Key。
         */
        private String secretKey;

        /**
         * 对象存储桶名称。
         */
        private String bucket;

        /**
         * 对象前缀，可为空。
         */
        private String objectPrefix = "";
    }
}
