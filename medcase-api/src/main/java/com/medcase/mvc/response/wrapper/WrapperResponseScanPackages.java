package com.medcase.mvc.response.wrapper;

import java.util.Collection;

/**
 * @author suyh
 * @since 2025-05-17
 */
public interface WrapperResponseScanPackages {
    /**
     * 需要支持自动封装的controller 的扫描包路径
     */
    Collection<String> getScanPackages();
}
