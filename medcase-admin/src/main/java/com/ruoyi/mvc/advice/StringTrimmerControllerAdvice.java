package com.ruoyi.mvc.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 处理http 请求参数 String 类型的前后空白字符
 *
 * @author suyh
 * @since 2024-09-13
 */
@ControllerAdvice
@Slf4j
public class StringTrimmerControllerAdvice {

    @InitBinder
    public void trimBinder(WebDataBinder binder) {
        // 仅适用于 @ModelAttribute 和 @RequestParam 绑定
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
