package com.ruoyi.web.controller.file;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.mvc.authentication.annotation.CurrLoginUser;
import com.ruoyi.storage.pojo.FileAttachment;
import com.ruoyi.storage.service.FileStorageService;
import com.ruoyi.storage.service.StoredFileContent;
import com.ruoyi.system.event.UserAvatarUploadedEvent;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 独立文件存储接口。
 *
 * @author suyh
 */
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileStorageController {
    private static final String DEFAULT_BUSINESS = "common";
    private static final String CASE_BUSINESS = "case";
    private static final String AVATAR_BUSINESS = "avatar";
    private static final String NOTICE_BUSINESS = "notice";

    private final FileStorageService fileStorageService;
    private final ApplicationContext applicationContext;

    @PostMapping("/upload/case")
    public R<FileAttachment> uploadCaseAttachment(
            @CurrLoginUser LoginUser doctorUser,
            @RequestParam("file") MultipartFile file) {
        UserTypeEnums userType = doctorUser.getUser().getUserType();
        return R.ofSuccess(fileStorageService.upload(
                file, CASE_BUSINESS, userType, doctorUser.getUserId()));
    }

    @PostMapping("/upload/avatar")
    public R<FileAttachment> uploadAvatar(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @RequestParam("file") MultipartFile file) {
        FileAttachment attachment = fileStorageService.upload(
                file, AVATAR_BUSINESS, UserTypeEnums.ADMIN, adminUser.getUserId());
        applicationContext.publishEvent(new UserAvatarUploadedEvent(
                this, adminUser, attachment.getFilePath()));
        return R.ofSuccess(attachment);
    }

    @PostMapping("/upload/notice")
    public R<FileAttachment> uploadNoticeImage(
            @CurrLoginUser(userType = UserTypeEnums.ADMIN) LoginUser adminUser,
            @RequestParam("file") MultipartFile file) {
        return R.ofSuccess(fileStorageService.upload(
                file, NOTICE_BUSINESS, UserTypeEnums.ADMIN, adminUser.getUserId()));
    }

    @PostMapping("/upload")
    public R<FileAttachment> upload(
            @CurrLoginUser(userType = UserTypeEnums.DOCTOR) LoginUser doctorUser,
            @RequestParam("file") MultipartFile file) {
        UserTypeEnums userType = doctorUser.getUser().getUserType();
        return R.ofSuccess(fileStorageService.upload(
                file, DEFAULT_BUSINESS, userType, doctorUser.getUserId()));
    }

    @GetMapping("/download")
    public void download(
            @RequestParam("filePath") String filePath,
            @RequestParam(value = "originalFilename", required = false) String originalFilename,
            HttpServletResponse response)
            throws IOException {
        try (StoredFileContent content = fileStorageService.download(filePath)) {
            FileAttachment file = content.getFile();
            response.setContentType(file.getContentType());
            response.setContentLengthLong(file.getSize());
            response.setHeader(
                    HttpHeaders.CONTENT_DISPOSITION,
                    ContentDisposition.attachment()
                            .filename(downloadFilename(originalFilename, file), StandardCharsets.UTF_8)
                            .build()
                            .toString());
            content.getInputStream().transferTo(response.getOutputStream());
        }
    }

    private String downloadFilename(String originalFilename, FileAttachment file) {
        if (originalFilename == null || originalFilename.isBlank()) {
            return file.getOriginalFilename();
        }
        String filename = originalFilename.replace('\\', '/');
        int slashIndex = filename.lastIndexOf('/');
        filename = slashIndex >= 0 ? filename.substring(slashIndex + 1) : filename;
        return filename.replaceAll("[\\r\\n]", "");
    }
}
