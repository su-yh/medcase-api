package com.medcase.web.controller.system.dto;

import com.medcase.common.utils.json.JsonUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoticeTopItemResponseTest {

    @Test
    void exposesOnlyReadOnlyIsReadProperty() {
        NoticeTopItemResponse response = new NoticeTopItemResponse();
        response.setRead(true);

        String json = JsonUtils.toJSONString(response);

        assertTrue(json.contains("\"isRead\":true"));
        assertFalse(json.contains("\"read\""));

        NoticeTopItemResponse parsed = JsonUtils.parseObject(
                "{\"isRead\":true}", NoticeTopItemResponse.class);
        assertFalse(parsed.isRead());
    }
}
