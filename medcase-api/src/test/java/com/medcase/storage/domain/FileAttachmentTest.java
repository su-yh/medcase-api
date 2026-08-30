package com.medcase.storage.domain;

import com.medcase.common.utils.json.JsonUtils;
import com.medcase.storage.pojo.FileAttachment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileAttachmentTest {
    @Test
    void ignoresRemovedFileNameField() {
        FileAttachment attachment = JsonUtils.parseObject(
                "{\"fileName\":\"20260827/report.pdf\",\"originalFilename\":\"report.pdf\"}",
                FileAttachment.class);

        assertEquals(null, attachment.getFilePath());

        String json = JsonUtils.toJSONString(attachment);
        assertTrue(json.contains("\"originalFilename\":\"report.pdf\""));
        assertFalse(json.contains("\"fileName\""));
    }
}
