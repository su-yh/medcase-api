package com.ruoyi.mvc.exception;

import com.ruoyi.mvc.error.IErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

import java.io.Serial;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author suyh
 * @since 2024-10-21
 */
@RequiredArgsConstructor
@Getter
@Slf4j
public abstract class AbstractBusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -4365987799788427525L;

    // 这里使用map 主要是懒得处理并发问题
    private static final Map<Class<? extends IErrorCode>, Integer> CLASS_MAP = new ConcurrentHashMap<>();
    private static final Set<Integer> CODE_SET = new HashSet<>();

    private final IErrorCode ec;

    private final ExceptionCategory category;

    // errorCode 中msg 占位符中对应的参数
    private final Object[] params;

    public AbstractBusinessException(@NonNull ExceptionCategory category, IErrorCode ec, Object... params) {
        // 这样将会在message 里面显示该值。
        super(category.name() + " EXCEPTION");

        this.category = category;
        this.ec = ec;
        this.params = params;
        // 这个是使用log4j 来解析 {} 的方法，注释掉，但不要删除，免得找不到了。
        // String message = org.slf4j.helpers.MessageFormatter.arrayFormat(errorCode.getMsg(), params).getMessage();

        // 校验是否有重复的，以及是否非枚举类型
        Class<? extends IErrorCode> cls = ec.getClass();
        if (!cls.isEnum()) {
            log.warn("NO ENUM Class: {}", cls.getName());
        }
    }
}
