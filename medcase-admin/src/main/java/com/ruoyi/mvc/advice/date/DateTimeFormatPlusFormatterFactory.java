package com.ruoyi.mvc.advice.date;

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
 * @author suyh
 * @since 2026-08-22
 */
public class DateTimeFormatPlusFormatterFactory implements AnnotationFormatterFactory<DateTimeFormatPlus> {

    @Override
    public Set<Class<?>> getFieldTypes() {
        // 仅支持 java.util.Date
        return Set.of(Date.class);
    }

    @Override
    public Printer<?> getPrinter(DateTimeFormatPlus annotation, Class<?> fieldType) {
        // 只做入参解析，不做格式化输出
        return null;
    }

    @Override
    public Parser<?> getParser(DateTimeFormatPlus anno, Class<?> fieldType) {
        final int offsetAmount = anno.offset();
        final OffsetUnit unit = anno.unit();

        return (String text, Locale locale) -> {
            if (text == null || text.isBlank()) {
                return null;
            }
            // 获取当前请求上下文时区
            TimeZone timeZone = LocaleContextHolder.getTimeZone();

            DateFormatter dateFormatter = new DateFormatter();
            if (!anno.pattern().isBlank()) {
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
