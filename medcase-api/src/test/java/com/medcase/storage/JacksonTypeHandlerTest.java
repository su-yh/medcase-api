package com.medcase.storage;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.medcase.biz.domain.DoctorCaseEntity;
import com.medcase.storage.pojo.FileAttachment;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonTypeHandlerTest {
    @Test
    void parsesSingleAttachmentJsonWithJacksonTypeHandler() {
        JacksonTypeHandler typeHandler = new JacksonTypeHandler(FileAttachment.class);
        FileAttachment attachment = (FileAttachment) typeHandler.parse(
                "{\"filePath\":\"avatar/20260828/user.png\",\"originalFilename\":\"user.png\"}");

        assertEquals("avatar/20260828/user.png", attachment.getFilePath());
        assertEquals("user.png", attachment.getOriginalFilename());
    }

    @Test
    @SuppressWarnings("unchecked")
    void parsesAttachmentListJsonWithJacksonTypeHandler() throws NoSuchFieldException {
        Field attachmentsField = DoctorCaseEntity.class.getDeclaredField("attachments");
        TableField tableField = attachmentsField.getAnnotation(TableField.class);
        assertEquals(JacksonTypeHandler.class, tableField.typeHandler());

        JacksonTypeHandler typeHandler = new JacksonTypeHandler(List.class, attachmentsField);
        List<FileAttachment> attachments = (List<FileAttachment>) typeHandler.parse(
                "[{\"filePath\":\"case/20260828/report.pdf\",\"originalFilename\":\"report.pdf\"}]");

        assertEquals(1, attachments.size());
        assertEquals("case/20260828/report.pdf", attachments.get(0).getFilePath());
    }
}
