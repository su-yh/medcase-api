package com.medcase.storage.enums;

import com.medcase.common.enums.BaseEnum;
import lombok.Getter;

/**
 * 文件上传业务类型。
 *
 * @author suyh
 */
@Getter
public enum FileBusinessEnums implements BaseEnum {
    CASE("case", "病例"),
    AVATAR("avatar", "头像"),
    NOTICE("notice", "公告"),
    CASE_REGISTER("case-register", "病例端注册");

    private final String code;
    private final String desc;

    FileBusinessEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
