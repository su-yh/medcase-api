package com.ruoyi.storage;

import com.ruoyi.mvc.constants.enums.ErrorCodeEnums;
import com.ruoyi.mvc.exception.AbstractBusinessException;
import com.ruoyi.storage.pojo.FileAttachment;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.storage.config.FileStorageProperties;
import com.ruoyi.storage.service.impl.MinioFileStorageService;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MinioFileStorageServiceExceptionTest {
    @Test
    void rejectsEmptyUploadWithBusinessException() {
        MinioFileStorageService service = new MinioFileStorageService(
                new FileStorageProperties(), mock(MinioClient.class));

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> service.upload(new MockMultipartFile("file", new byte[0]),
                        "case",
                        UserTypeEnums.DOCTOR, 12L));

        AbstractBusinessException businessException = assertInstanceOf(
                AbstractBusinessException.class, exception);
        assertEquals(ErrorCodeEnums.ATTACHMENT_EMPTY, businessException.getEc());
    }

    @Test
    void doesNotExposeStorageFailureDetailsToClient() throws Exception {
        FileStorageProperties properties = new FileStorageProperties();
        properties.getMinio().setBucket("attachment");
        MinioClient minioClient = mock(MinioClient.class);
        doThrow(new IOException("internal minio detail"))
                .when(minioClient)
                .putObject(any(PutObjectArgs.class));
        MinioFileStorageService service = new MinioFileStorageService(properties, minioClient);

        AbstractBusinessException exception = org.junit.jupiter.api.Assertions.assertThrows(
                AbstractBusinessException.class,
                () -> service.upload(new MockMultipartFile(
                        "file", "report.txt", "text/plain", "content".getBytes()),
                        "case",
                        UserTypeEnums.DOCTOR, 12L));

        assertEquals(ErrorCodeEnums.ATTACHMENT_UPLOAD_FAILED, exception.getEc());
        org.junit.jupiter.api.Assertions.assertEquals(0, exception.getParams().length);
    }

    @Test
    void prefixesUploadedFilePathWithBusinessDateUserAndUserId() throws Exception {
        FileStorageProperties properties = new FileStorageProperties();
        properties.getMinio().setBucket("attachment");
        MinioClient minioClient = mock(MinioClient.class);
        MinioFileStorageService service = new MinioFileStorageService(properties, minioClient);

        FileAttachment attachment = service.upload(new MockMultipartFile(
                "file", "report.txt", "text/plain", "content".getBytes()),
                "case",
                UserTypeEnums.DOCTOR, 12L);

        org.mockito.ArgumentCaptor<PutObjectArgs> captor =
                org.mockito.ArgumentCaptor.forClass(PutObjectArgs.class);
        org.mockito.Mockito.verify(minioClient).putObject(captor.capture());
        org.junit.jupiter.api.Assertions.assertTrue(
                captor.getValue().object().startsWith(
                        "case/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                                + "-01-12/"));
        org.junit.jupiter.api.Assertions.assertTrue(
                attachment.getFilePath().startsWith(
                        "case/" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                                + "-01-12/"));
        assertEquals("report.txt", attachment.getOriginalFilename());
    }

    @Test
    void usesOriginalFilenameFromMinioMetadataWhenDownloading() throws Exception {
        FileStorageProperties properties = new FileStorageProperties();
        properties.getMinio().setBucket("attachment");
        MinioClient minioClient = mock(MinioClient.class);
        StatObjectResponse stat = mock(StatObjectResponse.class);
        GetObjectResponse inputStream = mock(GetObjectResponse.class);
        when(stat.contentType()).thenReturn("application/pdf");
        when(stat.size()).thenReturn(128L);
        when(stat.object()).thenReturn(
                "01-12/20260827/0123456789abcdef0123456789abcdef-generated.pdf");
        when(stat.userMetadata()).thenReturn(Map.of(
                "original-filename", "病例 报告.pdf"));
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(stat);
        when(minioClient.getObject(any())).thenReturn(inputStream);
        MinioFileStorageService service = new MinioFileStorageService(properties, minioClient);

        try (var content = service.download(
                "01-12/20260827/0123456789abcdef0123456789abcdef-病例报告.pdf")) {
            assertEquals("病例 报告.pdf", content.getFile().getOriginalFilename());
            assertEquals(
                    "01-12/20260827/0123456789abcdef0123456789abcdef-病例报告.pdf",
                    content.getFile().getFilePath());
            assertEquals("application/pdf", content.getFile().getContentType());
            assertEquals(128L, content.getFile().getSize());
        }
    }

    @Test
    void usesMinioObjectFilenameWhenOriginalFilenameMetadataIsMissing() throws Exception {
        FileStorageProperties properties = new FileStorageProperties();
        properties.getMinio().setBucket("attachment");
        MinioClient minioClient = mock(MinioClient.class);
        StatObjectResponse stat = mock(StatObjectResponse.class);
        GetObjectResponse inputStream = mock(GetObjectResponse.class);
        when(stat.contentType()).thenReturn("application/pdf");
        when(stat.size()).thenReturn(128L);
        when(stat.object()).thenReturn(
                "01-12/20260827/0123456789abcdef0123456789abcdef-generated.pdf");
        when(stat.userMetadata()).thenReturn(Map.of());
        when(minioClient.statObject(any(StatObjectArgs.class))).thenReturn(stat);
        when(minioClient.getObject(any())).thenReturn(inputStream);
        MinioFileStorageService service = new MinioFileStorageService(properties, minioClient);

        try (var content = service.download(
                "01-12/20260827/0123456789abcdef0123456789abcdef-report.pdf")) {
            assertEquals(
                    "0123456789abcdef0123456789abcdef-generated.pdf",
                    content.getFile().getOriginalFilename());
        }
    }
}
