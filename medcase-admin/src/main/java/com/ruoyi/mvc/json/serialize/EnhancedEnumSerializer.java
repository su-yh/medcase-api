package com.ruoyi.mvc.json.serialize;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.JsonValueSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * 增加处理示例
 *
 * @author suyh
 * @since 2025-07-01
 */
@Slf4j
public class EnhancedEnumSerializer extends JsonSerializer<Enum<?>> {
    private final JsonSerializer<Enum<?>> defaultSerializer;

    public EnhancedEnumSerializer(JsonSerializer<Enum<?>> defaultSerializer) {
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize(Enum<?> enumValue, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        log.info("Enum serialize: {}, enum value: {}", EnhancedEnumSerializer.class.getSimpleName(), enumValue);
        if (enumValue == null) {
            gen.writeNull();
            return;
        }

        try {
            Field field = enumValue.getClass().getField(enumValue.name());
            JsonIgnore annotation = field.getAnnotation(JsonIgnore.class);
            if (annotation != null && annotation.value()) {
                gen.writeNull();
                return;
            }
        } catch (NoSuchFieldException e) {
            log.warn("get field failed.", e);
        }

        defaultSerializer.serialize(enumValue, gen, serializers);
    }

    public static BeanSerializerModifier buildModifier() {
        return new Modifier();
    }

    // 注册器
    public static class Modifier extends BeanSerializerModifier {
        @Override
        public JsonSerializer<?> modifySerializer(
                SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            if (beanDesc.getBeanClass().isEnum() && serializer instanceof JsonValueSerializer) {
                return new EnhancedEnumSerializer((JsonSerializer<Enum<?>>) serializer);
            }
            return serializer;
        }

        /**
         * suyh - 不要使用这种方式来处理枚举序列化的修改，因为它会受到注解 @{@link JsonValue} 的影响，jackson 在处理枚举序列化时，如果有该注解则估优先使用对应的序列器器。所以就不会走到此方法此。所以，最好是直接使用上面的方法来处理，以兼容全部的能力。
         * <p>
         * 不过，如果对该注解有特别和处理，同时对无该注解的又有不同的处理，那么还是可以两者分别实现的
         */
        @Override
        public JsonSerializer<?> modifyEnumSerializer(
                SerializationConfig config, JavaType type, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            if (type.isEnumType()) {
                return new EnhancedEnumSerializer((JsonSerializer<Enum<?>>) serializer);
            }
            return serializer;
        }
    }
}
