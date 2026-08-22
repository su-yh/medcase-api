package com.ruoyi.mvc.advice;

import com.ruoyi.web.controller.doctor.request.DoctorCasePageRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.ResolvableType;
import org.springframework.web.bind.WebDataBinder;

import java.beans.PropertyEditor;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DateOffsetBindingTest {
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    @Test
    void registersDateEditorsBeforeTargetObjectIsConstructed() {
        WebDataBinder binder = new WebDataBinder(null, "doctorCasePageRequest");
        binder.setTargetType(ResolvableType.forClass(DoctorCasePageRequest.class));
        new BindingControllerAdvice().initBinder(binder);

        assertNull(binder.getTarget());
        PropertyEditor lowerEditor = binder.findCustomEditor(Date.class, "createTimeLowerBound");
        PropertyEditor upperEditor = binder.findCustomEditor(Date.class, "createTimeUpperBound");

        assertNotNull(lowerEditor);
        assertNotNull(upperEditor);
        assertInstanceOf(DateOffsetEditor.class, lowerEditor);
        assertInstanceOf(DateOffsetEditor.class, upperEditor);

        lowerEditor.setAsText("2026-08-21");
        upperEditor.setAsText("2026-08-22");

        assertEquals("2026-08-21", format((Date) lowerEditor.getValue()));
        assertEquals("2026-08-23", format((Date) upperEditor.getValue()));
    }

    private static String format(Date date) {
        return new SimpleDateFormat(DATE_PATTERN).format(date);
    }
}
