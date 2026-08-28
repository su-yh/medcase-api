package com.ruoyi.storage.enums;

import com.ruoyi.common.enums.BaseEnum;
import lombok.Getter;

/**
 * 文件上传业务类型。
 *
 * @author suyh
 */
@Getter
public enum FileBusinessEnums implements BaseEnum {
    CASE("case"),
    AVATAR("avatar"),
    NOTICE("notice");

    private final String code;

    FileBusinessEnums(String code) {
        this.code = code;
    }
}
