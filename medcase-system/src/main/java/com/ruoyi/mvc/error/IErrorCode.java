package com.ruoyi.mvc.error;

/**
 * @author suyh
 * @since 2025-05-16
 */
public interface IErrorCode {
    String ERROR_CODE_PREFIX = "error.code";

    /**
     * 留给系统的错误码范围：[1000_000, 2000_000)
     * 业务相关的错误码范围：[2000_000, +∞)
     */
    int getCode();
    String getMsg();
}
