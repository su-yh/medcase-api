package com.medcase.mvc.util;

import com.medcase.mvc.constants.enums.ErrorCodeEnums;
import com.medcase.mvc.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.Expression;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author suyh
 * @since 2024-10-10
 */
@Slf4j
public class SpelParserUtils {
    private static final SpelExpressionParser parser = new SpelExpressionParser();

    private static final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    /**
     * 解析表达式
     *
     * @param expression 表达式
     * @param rootObject 用于计算表达式的根对象
     * @return 表达式计算结果
     */
    @Nullable
    public static Object parse(EvaluationContext context, @Language("SpEL") String expression, Object rootObject) {
        try {
            log.debug("======> Parse expression [{}]", expression);
            Expression parsed = expressionCache.computeIfAbsent(expression, parser::parseExpression);
            Object value = parsed.getValue(context, rootObject, Object.class);
            log.debug("======> Parse result [{}]", value);
            return value;
        } catch (ParseException | EvaluationException e) {
            log.error("Parse expression error, expression [{}]", expression, e);
            throw ExceptionUtil.business(ErrorCodeEnums.SERVICE_ERROR);
        }
    }

    /**
     * 解析表达式
     *
     * @param <T>          返回值类型
     * @param expression   表达式
     * @param rootObject   用于计算表达式的根对象
     * @param requiredType 指定返回值的类型
     * @return 表达式计算结果
     */
    @NonNull
    public static <T> T parse(EvaluationContext context, @Language("SpEL") String expression, Object rootObject, Class<T> requiredType) {
        Object any = parse(context, expression, rootObject);
        if (any == null) {
            log.error("Expression [" + expression + "] calculate result can not be null");
            throw ExceptionUtil.business(ErrorCodeEnums.SERVICE_ERROR);
        }
        if (!requiredType.isInstance(any)) {
            log.error("Expression [" + expression + "] calculate result must be [" + requiredType.getName() + "]");
            throw ExceptionUtil.business(ErrorCodeEnums.SERVICE_ERROR);
        }
        //noinspection unchecked
        return (T) any;
    }
}
