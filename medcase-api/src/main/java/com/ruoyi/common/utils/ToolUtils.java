package com.ruoyi.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author suyh
 * @since 2026-08-21
 */
public class ToolUtils {
    /**
     * 获取exception的详细错误信息。
     */
    public static String getExceptionMessage(Throwable e)
    {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
