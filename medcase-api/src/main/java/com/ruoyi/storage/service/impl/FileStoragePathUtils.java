package com.ruoyi.storage.service.impl;

import com.ruoyi.common.enums.UserTypeEnums;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 生成安全且不依赖用户输入的文件路径。
 *
 * @author suyh
 */
final class FileStoragePathUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final Pattern SPECIAL_CHARACTER_PATTERN = Pattern.compile("[^\\p{L}\\p{N}]");

    private FileStoragePathUtils() {
    }

    static String createPath(
            String business, UserTypeEnums userType, Long userId, String originalFilename) {
        String extension = extensionOf(originalFilename);
        String filename = filenameOf(originalFilename);
        String prefix = filename;
        int dotIndex = filename.indexOf('.');
        if (dotIndex >= 0) {
            prefix = filename.substring(0, dotIndex);
        }
        prefix = SPECIAL_CHARACTER_PATTERN.matcher(prefix).replaceAll("");
        if (prefix.length() > 10) {
            prefix = prefix.substring(0, 10);
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        return business + "/" + LocalDate.now().format(DATE_FORMATTER)
                + "-" + userType.getCode() + "-" + userId
                + "/" + uuid + "-" + prefix + extension;
    }

    static String extensionOf(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "";
        }

        String filename = filenameOf(originalFilename);
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex <= 0 || dotIndex == filename.length() - 1) {
            return "";
        }

        String extension = filename.substring(dotIndex);
        return extension.matches("\\.[A-Za-z0-9]{1,20}") ? extension : "";
    }

    private static String filenameOf(String originalFilename) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return "";
        }

        String filename = originalFilename.replace('\\', '/');
        int slashIndex = filename.lastIndexOf('/');
        return slashIndex >= 0 ? filename.substring(slashIndex + 1) : filename;
    }

    static String filenameOfPath(String filePath) {
        int slashIndex = filePath.lastIndexOf('/');
        return slashIndex >= 0 ? filePath.substring(slashIndex + 1) : filePath;
    }
}
