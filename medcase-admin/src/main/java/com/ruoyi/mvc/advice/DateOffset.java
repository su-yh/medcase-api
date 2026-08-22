package com.ruoyi.mvc.advice;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *     @ApiModelProperty("编制时间范围，上限。格式：yyyy-MM-dd")
 *     @JsonFormat(pattern = "yyyy-MM-dd")
 *     @JSONField(format = "yyyy-MM-dd")
 *     @DateOffset
 *     private Date createUpperBound;
 *
 * @author suyh
 * @since 2026-08-21
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DateOffset {
    /**
     * 字段描述（用于日志/错误信息）
     */
    String description() default "日期";

    boolean required() default false;
}
