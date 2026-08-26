package com.ruoyi.common.core.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class RTest
{
    @Test
    void successShouldExposeTypedDataAndStableJsonFields()
    {
        R<String> result = R.ofSuccess("token");

        assertEquals(0, result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertEquals("token", result.getData());

        JsonNode json = new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(result));
        assertEquals(0, json.get("code").intValue());
        assertEquals("操作成功", json.get("msg").textValue());
        assertEquals("token", json.get("data").textValue());
    }

    @Test
    void failureShouldKeepCodeAndMessageWithoutData()
    {
        R<Void> result = R.ofFail(401, "认证失败");

        assertEquals(401, result.getCode());
        assertEquals("认证失败", result.getMsg());
        assertNull(result.getData());
    }
}
