package com.ruoyi.web.controller.file;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.enums.UserTypeEnums;
import com.ruoyi.storage.pojo.FileAttachment;
import com.ruoyi.storage.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PostMapping;

import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileStorageControllerTest {
    @Test
    void caseUploadUsesFixedCaseBusiness() throws Exception {
        FileStorageService fileStorageService = mock(FileStorageService.class);
        FileStorageController controller = new FileStorageController(fileStorageService);
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

    private LoginUser doctorLoginUser() {
        SysUser user = new SysUser();
        user.setUserId(12L);
        user.setUserType(UserTypeEnums.DOCTOR);

        LoginUser loginUser = new LoginUser(user, Set.of());
        loginUser.setUserId(12L);
        return loginUser;
    }
}
