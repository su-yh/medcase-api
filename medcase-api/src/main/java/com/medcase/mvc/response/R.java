package com.medcase.mvc.response;

import lombok.Getter;

/**
 * 统一接口响应对象。
 *
 * @param <T> 响应数据类型
 */
@Getter
public class R<T> {

    public static final String SUCCESS_CODE = "OK";
    public static final String SUCCESS_MSG = "操作成功";

    private final String code;
    private final String msg;
    private final T data;

    protected R(String code, String msg, T data) {
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

    public static Object ofFail(String code, String message) {
        return new R<>(code, message, null);
    }
}
