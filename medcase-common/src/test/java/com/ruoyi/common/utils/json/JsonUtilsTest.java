package com.ruoyi.common.utils.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import org.junit.jupiter.api.Test;

class JsonUtilsTest
{
    @Test
    void parseObjectShouldIgnoreUnknownProperties()
    {
        String json = "{\"name\":\"doctor\",\"extra\":\"ignored\"}";

        DoctorInfo result = JsonUtils.parseObject(json, DoctorInfo.class);

        assertEquals("doctor", result.getName());
    }

    @Test
    void toJSONStringShouldMatchLegacyDateFormat()
    {
        String json = JsonUtils.toJSONString(new Date(0L));

        assertEquals("\"1970-01-01 08:00:00\"", json);
    }

    static class DoctorInfo
    {
        private String name;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }
}
