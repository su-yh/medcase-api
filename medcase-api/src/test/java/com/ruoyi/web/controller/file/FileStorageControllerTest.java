package com.ruoyi.web.controller.file;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.storage.pojo.FileAttachment;
import com.ruoyi.storage.service.FileStorageService;
import com.ruoyi.system.event.UserAvatarUploadedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

class FileStorageControllerTest {
    @Test
    void caseUploadUsesFixedCaseBusiness() throws Exception {
        FileStorageService fileStorageService = mock(FileStorageService.class);
        FileStorageController controller = controller(fileStorageService);
        MockMultipartFile file = new MockMultipartFile(
                "file", "report.pdf", "application/pdf", "content".getBytes());
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("case/20260827-01-12/generated-report.pdf");
        when(fileStorageService.upload(file, "case", UserTypeEnums.DOCTOR, 12L))
                .thenReturn(attachment);

        Method method = FileStorageController.class.getDeclaredMethod(
                "uploadCaseAttachment", LoginUser.class, org.springframework.web.multipart.MultipartFile.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        assertNotNull(postMapping);
        assertEquals("/upload/case", postMapping.value()[0]);

        R<FileAttachment> result = (R<FileAttachment>) method.invoke(
                controller, doctorLoginUser(), file);

        assertEquals(attachment, result.getData());
        verify(fileStorageService).upload(file, "case", UserTypeEnums.DOCTOR, 12L);
    }

    @Test
    void avatarUploadUsesFixedAvatarBusinessAndUpdatesCurrentUser() throws Exception {
        FileStorageService fileStorageService = mock(FileStorageService.class);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        FileStorageController controller = new FileStorageController(
                fileStorageService, applicationContext);
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "content".getBytes());
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("avatar/20260827-00-12/generated-avatar.png");
        when(fileStorageService.upload(file, "avatar", UserTypeEnums.ADMIN, 12L))
                .thenReturn(attachment);
        LoginUser adminUser = adminLoginUser();

        Method method = FileStorageController.class.getDeclaredMethod(
                "uploadAvatar", LoginUser.class, org.springframework.web.multipart.MultipartFile.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        assertNotNull(postMapping);
        assertEquals("/upload/avatar", postMapping.value()[0]);

        R<FileAttachment> result = (R<FileAttachment>) method.invoke(controller, adminUser, file);

        assertEquals(attachment, result.getData());
        verify(fileStorageService).upload(file, "avatar", UserTypeEnums.ADMIN, 12L);
        var eventCaptor = forClass(UserAvatarUploadedEvent.class);
        verify(applicationContext).publishEvent(eventCaptor.capture());
        UserAvatarUploadedEvent event = eventCaptor.getValue();
        assertInstanceOf(org.springframework.context.ApplicationEvent.class, event);
        assertEquals(adminUser, event.getLoginUser());
        assertEquals(attachment.getFilePath(), event.getFilePath());
    }

    @Test
    void noticeUploadUsesFixedNoticeBusiness() throws Exception {
        FileStorageService fileStorageService = mock(FileStorageService.class);
        FileStorageController controller = controller(fileStorageService);
        MockMultipartFile file = new MockMultipartFile(
                "file", "notice.png", "image/png", "content".getBytes());
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("notice/20260827-00-12/notice.png");
        when(fileStorageService.upload(file, "notice", UserTypeEnums.ADMIN, 12L))
                .thenReturn(attachment);

        Method method = FileStorageController.class.getDeclaredMethod(
                "uploadNoticeImage", LoginUser.class, org.springframework.web.multipart.MultipartFile.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        assertNotNull(postMapping);
        assertEquals("/upload/notice", postMapping.value()[0]);

        R<FileAttachment> result = (R<FileAttachment>) method.invoke(
                controller, adminLoginUser(), file);

        assertEquals(attachment, result.getData());
        verify(fileStorageService).upload(file, "notice", UserTypeEnums.ADMIN, 12L);
    }

    private FileStorageController controller(FileStorageService fileStorageService) {
        return new FileStorageController(
                fileStorageService, mock(ApplicationContext.class));
    }

    private LoginUser doctorLoginUser() {
        SysUser user = new SysUser();
        user.setUserId(12L);
        user.setUserType(UserTypeEnums.DOCTOR);

        LoginUser loginUser = new LoginUser(user, Set.of());
        loginUser.setUserId(12L);
        return loginUser;
    }

    private LoginUser adminLoginUser() {
        SysUser user = new SysUser();
        user.setUserId(12L);
        user.setUserType(UserTypeEnums.ADMIN);

        LoginUser loginUser = new LoginUser(user, Set.of());
        loginUser.setUserId(12L);
        return loginUser;
    }
}
