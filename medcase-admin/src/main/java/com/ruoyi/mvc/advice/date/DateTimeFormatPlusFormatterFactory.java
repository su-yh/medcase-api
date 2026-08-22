package com.ruoyi.mvc.advice.date;

import org.jspecify.annotations.NonNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * 增强日期解析工厂：{@link DateTimeFormatPlus}
 * 复刻原生DateTimeFormat全部解析能力，并支持时间偏移 offset + unit
 * 仅支持 java.util.Date；仅用于请求参数解析(RequestParam/ModelAttribute)，不支持@RequestBody
 * @author suyh
 * @since 2026-08-22
 */
public class DateTimeFormatPlusFormatterFactory implements AnnotationFormatterFactory<DateTimeFormatPlus> {

    @Override
    @NonNull
    public Set<Class<?>> getFieldTypes() {
        return Set.of(Date.class);
    }

    @Override
    // @Nullable
    @NonNull
    public Printer<?> getPrinter(@NonNull DateTimeFormatPlus annotation, @NonNull Class<?> fieldType) {
        // 只做入参解析，不做输出格式化
        return null;
    }

    @Override
    @NonNull
    public Parser<?> getParser(@NonNull DateTimeFormatPlus anno, @NonNull Class<?> fieldType) {
        final int offsetAmount = anno.offset();
        final OffsetUnit unit = anno.unit();

        return (@NonNull String text, @NonNull Locale locale) -> {
            if (!StringUtils.hasLength(text)) {
                return null;
            }
            TimeZone timeZone = LocaleContextHolder.getTimeZone();

            DateFormatter dateFormatter = new DateFormatter();
            if (StringUtils.hasText(anno.pattern())) {
                dateFormatter.setPattern(anno.pattern());
            }
            if (anno.iso() != DateTimeFormat.ISO.NONE) {
                dateFormatter.setIso(anno.iso());
            }
            String styleStr = anno.style();
            if (StringUtils.hasLength(styleStr)) {
                dateFormatter.setStylePattern(styleStr);
            }
            dateFormatter.setTimeZone(timeZone);

            Date rawDate = dateFormatter.parse(text, locale);
            long offsetMs = unit.toMillis(offsetAmount);
            return new Date(rawDate.getTime() + offsetMs);
        };
    }
}

