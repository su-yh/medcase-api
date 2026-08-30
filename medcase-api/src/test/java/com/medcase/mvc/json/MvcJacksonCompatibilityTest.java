package com.medcase.mvc.json;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MvcJacksonCompatibilityTest {

    @Test
    void customizerConfiguresJackson3Mapper() throws Exception {
        JsonMapper.Builder builder = JsonMapper.builder();
        new JacksonBuilderCustomizer().customize(builder);
        JsonMapper mapper = builder.build();

        assertEquals("\"42\"", mapper.writeValueAsString(42L));
        assertEquals("trimmed", mapper.readValue("\"  trimmed  \"", String.class));
    }
}
