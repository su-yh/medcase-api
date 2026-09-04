package com.medcase.common.utils.sql;

import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import org.springframework.util.StringUtils;

/**
 * sql操作工具类
 * 
 */
public class SqlUtil {

    /**
     * 定义常用的 sql关键字
     */
    public static String SQL_REGEX = "\u000B|%0A|and |extractvalue|updatexml|sleep|information_schema|exec |insert |select |delete |update |drop |count |chr |mid |master |truncate |char |declare |or |union |like |+|/*|user()";

    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    public static String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    /**
     * 限制orderBy最大长度
     */
    private static final int ORDER_BY_MAX_LENGTH = 500;

    /**
     * 检查字符，防止注入绕过
     */
    public static String escapeOrderBySql(String value) {

        if (StringUtils.hasText(value) && !isValidOrderBySql(value)) {

            throw ExceptionUtil.business(ErrorCodeEnums.SQL_ORDER_BY_INVALID);
        }
        if (value != null && value.length() > ORDER_BY_MAX_LENGTH) {

            throw ExceptionUtil.business(ErrorCodeEnums.SQL_ORDER_BY_TOO_LONG);
        }
        return value;
    }

    /**
     * 验证 order by 语法是否符合规范
     */
    public static boolean isValidOrderBySql(String value) {

        return value.matches(SQL_PATTERN);
    }

    /**
     * SQL关键字检查
     */
    public static void filterKeyword(String value) {

        if (!StringUtils.hasText(value)) {

            return;
        }
        String normalizedValue = value.replaceAll("\\p{Z}|\\s", "");
        String[] sqlKeywords = StringUtils.tokenizeToStringArray(SQL_REGEX, "|", false, true);
        for (String sqlKeyword : sqlKeywords) {

            if (org.apache.commons.lang3.Strings.CI.indexOf(normalizedValue, sqlKeyword) > -1) {

                throw ExceptionUtil.business(ErrorCodeEnums.SQL_KEYWORD_INVALID, sqlKeyword);
            }
        }
    }
}
