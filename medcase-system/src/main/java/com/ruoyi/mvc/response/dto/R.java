package com.ruoyi.mvc.response.dto;

import lombok.Getter;

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
}
