package com.medcase.mvc.advice;

import com.medcase.common.utils.DateUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * 处理请求参数中的日期类型转换。
 */
@ControllerAdvice
public class DateControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {

        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {

            @Override
            public void setAsText(String text) {

                setValue(DateUtils.parseDate(text));
            }
        });
    }
}
