package com.medcase.common.utils.file;

import com.medcase.common.constant.Constants;
import com.medcase.common.utils.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * 本地文件路径工具类。
 */
public class FileUploadUtils {

    private static String defaultBaseDir;

    public static void setDefaultBaseDir(String defaultBaseDir) {

        FileUploadUtils.defaultBaseDir = defaultBaseDir;
    }

    public static String getDefaultBaseDir() {

        return defaultBaseDir;
    }

    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {

        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.exists()) {

            if (!desc.getParentFile().exists()) {

                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }

    public static final String getPathFileName(String uploadDir, String fileName) throws IOException {

        int dirLastIndex = defaultBaseDir.length() + 1;
        String currentDir = StringUtils.substring(uploadDir, dirLastIndex);
        return Constants.RESOURCE_PREFIX + "/" + currentDir + "/" + fileName;
    }

}
