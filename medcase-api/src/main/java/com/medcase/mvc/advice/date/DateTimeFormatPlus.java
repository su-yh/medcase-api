package com.medcase.mvc.advice.date;

import org.springframework.format.annotation.DateTimeFormat;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author suyh
 * @since 2026-08-22
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DateTimeFormatPlus {

    /**
     * 对应 {@link DateTimeFormat#pattern()}
     */
    String pattern() default "";

    /**
     * 对应 {@link DateTimeFormat#iso()}
     */
    DateTimeFormat.ISO iso() default DateTimeFormat.ISO.NONE;

    /**
     * 对应 {@link DateTimeFormat#style()}
     */
    String style() default "";

    /**
     * 时间偏移量，正数向后，负数向前
     */
    int offset() default 0;

    /**
     * 偏移单位 DAY / HOUR / MINUTE
     */
    OffsetUnit unit() default OffsetUnit.DAY;
}
