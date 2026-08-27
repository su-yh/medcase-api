package com.ruoyi.storage.service.impl;

import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.ExceptionUtil;
import com.ruoyi.storage.config.FileStorageProperties;
import com.ruoyi.storage.pojo.FileAttachment;
import com.ruoyi.storage.service.FileStorageService;
import com.ruoyi.storage.service.StoredFileContent;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

/**
 * MinIO 文件存储实现。
 *
 * @author suyh
 */
@RequiredArgsConstructor
@Slf4j
public class MinioFileStorageService implements FileStorageService {
    private final FileStorageProperties properties;
    private final MinioClient minioClient;

    @Override
    public FileAttachment upload(
            MultipartFile file, String business, UserTypeEnums userType, Long userId) {
        if (file == null || file.isEmpty()) {
            throw ExceptionUtil.business(ErrorCodeEnums.ATTACHMENT_EMPTY);
        }
        if (userType == null || userId == null) {
            throw ExceptionUtil.business(ErrorCodeEnums.USER_NOT_LOGIN);
        }

        String filePath = FileStoragePathUtils.createPath(
                business, userType, userId, file.getOriginalFilename());
        String objectName = objectName(filePath);
        String contentType = contentType(file.getContentType());
        String bucket = bucket();
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(contentType)
                    .userMetadata(Map.of(
                            "original-filename",
                            safeFilename(file.getOriginalFilename(), filePath)))
                    .build());
            FileAttachment attachment = new FileAttachment();
            attachment.setFilePath(filePath);
            attachment.setOriginalFilename(safeFilename(file.getOriginalFilename(), filePath));
            attachment.setContentType(contentType);
            attachment.setSize(file.getSize());
            return attachment;
        }
        catch (Exception e) {
            log.error("MinIO 文件上传失败", e);
            throw ExceptionUtil.business(ErrorCodeEnums.ATTACHMENT_UPLOAD_FAILED);
        }
    }

    @Override
    public StoredFileContent download(String filePath) {
        validateFilePath(filePath);
        String objectName = objectName(filePath);
        String bucket = bucket();
        try {
            StatObjectResponse stat = minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            GetObjectResponse inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .build());
            FileAttachment attachment = new FileAttachment();
            attachment.setFilePath(filePath);
            attachment.setOriginalFilename(originalFilename(stat));
            attachment.setContentType(contentType(stat.contentType()));
            attachment.setSize(stat.size());
            return new StoredFileContent(attachment, inputStream);
        }
        catch (Exception e) {
            log.error("MinIO 文件下载失败", e);
            throw ExceptionUtil.business(ErrorCodeEnums.ATTACHMENT_DOWNLOAD_FAILED);
        }
    }

    private String originalFilename(StatObjectResponse stat) {
        Map<String, String> userMetadata = stat.userMetadata();
        if (userMetadata != null) {
            String filename = userMetadata.get("original-filename");
            if (filename != null && !filename.isBlank()) {
                return filename;
            }
        }
        return FileStoragePathUtils.filenameOfPath(stat.object());
    }

    private String bucket() {
        String bucket = properties.getMinio().getBucket();
        if (bucket == null || bucket.isBlank()) {
            throw ExceptionUtil.business(ErrorCodeEnums.ATTACHMENT_BUCKET_NOT_CONFIGURED);
        }
        return bucket;
    }

    private String objectName(String filePath) {
        String prefix = properties.getMinio().getObjectPrefix();
        if (prefix == null || prefix.isBlank()) {
            return filePath;
        }
        return prefix.replaceAll("^/|/$", "") + "/" + filePath;
    }

    private void validateFilePath(String filePath) {
        if (filePath == null || filePath.isBlank() || filePath.startsWith("/")
                || filePath.contains("..") || filePath.contains("\\")) {
            throw ExceptionUtil.business(ErrorCodeEnums.ATTACHMENT_INVALID_PATH, filePath);
        }
    }

    private String contentType(String contentType) {
        return contentType == null || contentType.isBlank()
                ? "application/octet-stream"
                : contentType;
    }

    private String safeFilename(String originalFilename, String filePath) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return FileStoragePathUtils.filenameOfPath(filePath);
        }
        String filename = originalFilename.replace('\\', '/');
        int slashIndex = filename.lastIndexOf('/');
        return slashIndex >= 0 ? filename.substring(slashIndex + 1) : filename;
    }
}
