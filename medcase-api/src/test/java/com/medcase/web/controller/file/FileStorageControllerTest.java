package com.medcase.web.controller.file;

import com.medcase.mvc.response.R;
import com.medcase.common.core.domain.entity.SysUser;
import com.medcase.common.core.domain.model.LoginUser;
import com.medcase.common.enums.UserTypeEnums;
import com.medcase.storage.enums.FileBusinessEnums;
import com.medcase.storage.pojo.FileAttachment;
import com.medcase.storage.service.FileStorageService;
import com.medcase.system.event.UserAvatarUploadedEvent;
import com.medcase.common.annotation.Anonymous;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    void doctorRegistrationUploadIsAnonymousAndUsesFixedUserIdentity() throws Exception {
        FileStorageService fileStorageService = mock(FileStorageService.class);
        FileStorageController controller = controller(fileStorageService);
        MockMultipartFile file = new MockMultipartFile(
                "file", "id-card.png", "image/png", "content".getBytes());
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("doctor-register/20260829-01-0/id-card.png");
        when(fileStorageService.upload(
                file, FileBusinessEnums.DOCTOR_REGISTER, UserTypeEnums.DOCTOR, 0L))
                .thenReturn(attachment);

        Method method = FileStorageController.class.getDeclaredMethod(
                "uploadDoctorRegistration", org.springframework.web.multipart.MultipartFile.class);

        assertNotNull(method.getAnnotation(Anonymous.class));
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        assertNotNull(postMapping);
        assertEquals("/upload/doctor-register", postMapping.value()[0]);
        RequestParam fileParameter = method.getParameters()[0].getAnnotation(RequestParam.class);
        assertNotNull(fileParameter);
        assertEquals("file", fileParameter.value());

        R<FileAttachment> result = (R<FileAttachment>) method.invoke(controller, file);

        assertEquals(attachment, result.getData());
        verify(fileStorageService).upload(
                file, FileBusinessEnums.DOCTOR_REGISTER, UserTypeEnums.DOCTOR, 0L);
    }

    @Test
    void uploadUsesCaseBusinessFromQueryParameter() throws Exception {
        FileStorageService fileStorageService = mock(FileStorageService.class);
        FileStorageController controller = controller(fileStorageService);
        MockMultipartFile file = new MockMultipartFile(
                "file", "report.pdf", "application/pdf", "content".getBytes());
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("case/20260827-01-12/generated-report.pdf");
        when(fileStorageService.upload(file, FileBusinessEnums.CASE, UserTypeEnums.DOCTOR, 12L))
                .thenReturn(attachment);

        Method method = FileStorageController.class.getDeclaredMethod(
                "upload", LoginUser.class, FileBusinessEnums.class,
                org.springframework.web.multipart.MultipartFile.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        assertNotNull(postMapping);
        assertEquals("/upload", postMapping.value()[0]);
        RequestParam business = method.getParameters()[1].getAnnotation(RequestParam.class);
        assertNotNull(business);
        assertEquals("business", business.value());

        R<FileAttachment> result = (R<FileAttachment>) method.invoke(
                controller, doctorLoginUser(), FileBusinessEnums.CASE, file);

        assertEquals(attachment, result.getData());
        verify(fileStorageService).upload(file, FileBusinessEnums.CASE, UserTypeEnums.DOCTOR, 12L);
    }

    @Test
    void uploadUsesAvatarBusinessAndUpdatesCurrentUser() throws Exception {
        FileStorageService fileStorageService = mock(FileStorageService.class);
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        FileStorageController controller = new FileStorageController(
                fileStorageService, applicationContext);
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", "content".getBytes());
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("avatar/20260827-00-12/generated-avatar.png");
        when(fileStorageService.upload(file, FileBusinessEnums.AVATAR, UserTypeEnums.ADMIN, 12L))
                .thenReturn(attachment);
        LoginUser adminUser = adminLoginUser();

        Method method = FileStorageController.class.getDeclaredMethod(
                "upload", LoginUser.class, FileBusinessEnums.class,
                org.springframework.web.multipart.MultipartFile.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        assertNotNull(postMapping);
        assertEquals("/upload", postMapping.value()[0]);

        R<FileAttachment> result = (R<FileAttachment>) method.invoke(
                controller, adminUser, FileBusinessEnums.AVATAR, file);

        assertEquals(attachment, result.getData());
        verify(fileStorageService).upload(file, FileBusinessEnums.AVATAR, UserTypeEnums.ADMIN, 12L);
        var eventCaptor = forClass(UserAvatarUploadedEvent.class);
        verify(applicationContext).publishEvent(eventCaptor.capture());
        UserAvatarUploadedEvent event = eventCaptor.getValue();
        assertInstanceOf(org.springframework.context.ApplicationEvent.class, event);
        assertEquals(adminUser, event.getLoginUser());
        assertEquals(attachment, event.getAttachment());
    }

    @Test
    void uploadUsesNoticeBusiness() throws Exception {
        FileStorageService fileStorageService = mock(FileStorageService.class);
        FileStorageController controller = controller(fileStorageService);
        MockMultipartFile file = new MockMultipartFile(
                "file", "notice.png", "image/png", "content".getBytes());
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("notice/20260827-00-12/notice.png");
        when(fileStorageService.upload(file, FileBusinessEnums.NOTICE, UserTypeEnums.ADMIN, 12L))
                .thenReturn(attachment);

        Method method = FileStorageController.class.getDeclaredMethod(
                "upload", LoginUser.class, FileBusinessEnums.class,
                org.springframework.web.multipart.MultipartFile.class);
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        assertNotNull(postMapping);
        assertEquals("/upload", postMapping.value()[0]);

        R<FileAttachment> result = (R<FileAttachment>) method.invoke(
                controller, adminLoginUser(), FileBusinessEnums.NOTICE, file);

        assertEquals(attachment, result.getData());
        verify(fileStorageService).upload(file, FileBusinessEnums.NOTICE, UserTypeEnums.ADMIN, 12L);
    }

    @Test
    void uploadAllowsKnownBusinessForAnyUserType() throws Exception {
        FileStorageService fileStorageService = mock(FileStorageService.class);
        FileStorageController controller = controller(fileStorageService);
        MockMultipartFile file = new MockMultipartFile(
                "file", "avatar.png", "image/png", new byte[] {1});
        FileAttachment attachment = new FileAttachment();
        attachment.setFilePath("avatar/20260827-01-12/avatar.png");
        when(fileStorageService.upload(file, FileBusinessEnums.AVATAR, UserTypeEnums.DOCTOR, 12L))
                .thenReturn(attachment);
        Method method = FileStorageController.class.getDeclaredMethod(
                "upload", LoginUser.class, FileBusinessEnums.class,
                org.springframework.web.multipart.MultipartFile.class);

        R<FileAttachment> result = (R<FileAttachment>) method.invoke(
                controller, doctorLoginUser(), FileBusinessEnums.AVATAR, file);

        assertEquals(attachment, result.getData());
        verify(fileStorageService).upload(file, FileBusinessEnums.AVATAR, UserTypeEnums.DOCTOR, 12L);
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
