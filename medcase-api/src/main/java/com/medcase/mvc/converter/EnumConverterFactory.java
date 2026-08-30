package com.medcase.mvc.converter;

import com.medcase.common.enums.BaseEnum;
import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * @author suyh
 * @since 2026-08-21
 */
public class EnumConverterFactory implements ConverterFactory<String, BaseEnum> {

    @NonNull
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(@NonNull Class<T> targetType) {
        return new StringToEnumConverter<>(targetType);
    }

    private static class StringToEnumConverter<T extends BaseEnum> implements Converter<String, T> {
        private final Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(@NonNull String source) {
            return fromCode(enumType, source);
        }
    }

    private static <T extends BaseEnum> T fromCode(Class<T> enumClass, String code) {
        for (T enumConstant : enumClass.getEnumConstants()) {
            if (enumConstant.getCode().equals(code)) {
                return enumConstant;
            }
        }

        throw new IllegalArgumentException("Unknown code: " + code + " for enum: " + enumClass.getSimpleName());
    }
}

