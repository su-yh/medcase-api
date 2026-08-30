package com.medcase.mvc.error;

/**
 * @author suyh
 * @since 2025-05-16
 */
public interface IErrorCode {
    /**
     * 直接对应 i18n 资源文件中的消息 key。
     */
    String getCode();

    String getMsg();
}
