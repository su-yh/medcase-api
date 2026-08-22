package com.ruoyi.mvc.advice;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.lang.reflect.Field;

/**
 * @author suyh
 * @since 2026-08-21
 */
@ControllerAdvice
public class BindingControllerAdvice {
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        Object target = binder.getTarget();
        if (target == null) return;

        for (Field field : target.getClass().getDeclaredFields()) {
            DateOffset annotation = field.getAnnotation(DateOffset.class);
            if (annotation != null) {
                binder.registerCustomEditor(field.getType(), field.getName(), new DateOffsetEditor(annotation));
            }
        }
    }
}
