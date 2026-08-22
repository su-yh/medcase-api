package com.ruoyi.mvc.advice;

import java.beans.PropertyEditorSupport;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author suyh
 * @since 2026-08-21
 */
public class DateOffsetEditor extends PropertyEditorSupport {

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT =
            ThreadLocal.withInitial(() -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                return sdf;
            });

    private final DateOffset annotation;

    public DateOffsetEditor(DateOffset annotation) {
        this.annotation = annotation;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        if (text == null || text.trim().isEmpty()) {
            if (annotation.required()) {
                throw new IllegalArgumentException(annotation.description() + "不能为空");
            }
            setValue(null);
            return;
        }

        try {
            // 1. 解析前端传入的日期
            Date inputDate = DATE_FORMAT.get().parse(text.trim());

            // 2. 验证格式
            String formatted = DATE_FORMAT.get().format(inputDate);
            if (!formatted.equals(text.trim())) {
                throw new IllegalArgumentException(
                        annotation.description() + "格式错误，请使用 yyyy-MM-dd 格式"
                );
            }

            // 3. 计算偏移后的日期
            Date offsetDate = calculateOffset(inputDate);

            setValue(offsetDate);

        } catch (java.text.ParseException e) {
            throw new IllegalArgumentException(
                    annotation.description() + "格式错误，请使用 yyyy-MM-dd 格式"
            );
        }
    }

    /**
     * 计算偏移后的日期
     */
    private Date calculateOffset(Date inputDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(inputDate);
        calendar.add(Calendar.DAY_OF_YEAR, annotation.offsetDays());

        return calendar.getTime();
    }

    @Override
    public String getAsText() {
        Date value = (Date) getValue();
        if (value == null) {
            return "";
        }
        return DATE_FORMAT.get().format(value);
    }
}
