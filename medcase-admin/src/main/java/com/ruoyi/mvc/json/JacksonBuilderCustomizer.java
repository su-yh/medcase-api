package com.ruoyi.mvc.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.mvc.advice.BizStringDeserializer;
import com.ruoyi.mvc.json.serialize.EnhancedEnumSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

/**
 * @author suyh
 * @since 2025-07-02
 */
@Component
@Slf4j
public class JacksonBuilderCustomizer implements Jackson2ObjectMapperBuilderCustomizer {

    @Override
    public void customize(Jackson2ObjectMapperBuilder builder) {
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance)
                .addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addDeserializer(String.class, BizStringDeserializer.instance);
        builder.modules(simpleModule);

        // 它的时机是在所有配置都完成之后，最后调用。
        builder.postConfigurer(mapper -> {
            // {
            //     log.info("init json utils.");
            //     ObjectMapper utilMapper = mapper.copy();
            //
            //     // 同步 JsonUtils 与web 中使用的jackson 一致
            //     JsonUtils.initMapper(utilMapper);
            // }

            {
                // 对于spring的 jackson 需要与前端交互的，对枚举的序列化需要做增强处理。
                SimpleModule module = new SimpleModule();
                module.setSerializerModifier(EnhancedEnumSerializer.buildModifier());
                mapper.registerModule(module);
            }
        });
    }
}
