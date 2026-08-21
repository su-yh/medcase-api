package com.ruoyi.web.controller.common;

import lombok.Data;

@Data
public class UploadResponse {
    private String url;

    private String newFileName;

    private String originalFilename;
}
