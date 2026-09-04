package com.medcase.mvc.advice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.WebDataBinder;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DateControllerAdviceTest {

    @Test
    void bindsDateRequestParameterToDate() throws Exception {
        DateTarget target = new DateTarget();
        WebDataBinder binder = new WebDataBinder(target);
        Class<?> adviceType = Class.forName("com.medcase.mvc.advice.DateControllerAdvice");
        Object advice = adviceType.getConstructor().newInstance();
        Method initBinder = adviceType.getMethod("initBinder", WebDataBinder.class);

        initBinder.invoke(advice, binder);
        binder.bind(new MutablePropertyValues(Map.of("date", "2026-09-04")));

        assertNotNull(target.getDate());
    }

    public static class DateTarget {
        private Date date;

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }
}
