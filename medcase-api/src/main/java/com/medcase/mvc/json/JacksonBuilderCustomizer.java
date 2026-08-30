package com.medcase.mvc.json;

import com.medcase.mvc.advice.BizStringDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author suyh
 * @since 2025-07-02
 */
@Component
@Slf4j
public class JacksonBuilderCustomizer implements JsonMapperBuilderCustomizer {

    @Override
    public void customize(JsonMapper.Builder builder) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance)
                .addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addDeserializer(String.class, BizStringDeserializer.instance);
        builder.addModule(simpleModule);
    }
}
