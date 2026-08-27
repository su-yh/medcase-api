package com.ruoyi.storage.service;

import com.ruoyi.storage.pojo.FileAttachment;
import com.ruoyi.common.enums.UserTypeEnums;
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
            MultipartFile file, String business, UserTypeEnums userType, Long userId);

    /**
     * 根据文件相对路径读取文件内容。
     */
    StoredFileContent download(String filePath);
}
