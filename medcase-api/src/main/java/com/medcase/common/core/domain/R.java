package com.medcase.common.core.domain;

import com.medcase.common.constant.HttpStatus;
import lombok.Getter;

/**
 * 统一接口响应对象。
 * TODO: suyh - 把excelExport 处理好了，这个类要迁移一下了。
 *
 * @param <T> 响应数据类型
 */
@Getter
public class R<T> {

    public static final Integer SUCCESS_CODE = 0;
    public static final String SUCCESS_MSG = "操作成功";

    private final Integer code;
    private final String msg;
    private final T data;

    protected R(Integer code, String msg, T data) {

        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> R<T> ofSuccess(T data, String message) {

        return new R<>(SUCCESS_CODE, message, data);
    }

    public static <T> R<T> ofSuccess(T data) {

        return new R<>(SUCCESS_CODE, SUCCESS_MSG, data);
    }

    public static <T> R<T> ofSuccess() {

        return new R<>(SUCCESS_CODE, SUCCESS_MSG, null);
    }

    public static <T> R<T> ofFail(int code, String message) {

        return new R<>(code, message, null);
    }

    public static <T> R<T> ofFail(String message) {

        return ofFail(HttpStatus.ERROR, message);
    }
}
