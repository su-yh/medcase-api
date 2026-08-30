package com.medcase.storage.pojo;

import lombok.Data;

/**
 * File attachment
 *
 * @author suyh
 */
@Data
public class FileAttachment {
    private String filePath;

    private String originalFilename;

    private String contentType;

    private Long size;
}
