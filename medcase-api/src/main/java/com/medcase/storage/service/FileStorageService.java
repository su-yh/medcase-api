package com.medcase.storage.service;

import com.medcase.common.enums.UserTypeEnums;
import com.medcase.storage.enums.FileBusinessEnums;
import com.medcase.storage.pojo.FileAttachment;
import org.springframework.web.multipart.MultipartFile;

/**
 * 独立文件存储服务。
 *
 * @author suyh
 */
public interface FileStorageService {
    /**
     * 上传文件并返回文件元数据。
     */
    FileAttachment upload(
            MultipartFile file, FileBusinessEnums business, UserTypeEnums userType, Long userId);

    /**
     * 根据文件相对路径读取文件内容。
     */
    StoredFileContent download(String filePath);
}
